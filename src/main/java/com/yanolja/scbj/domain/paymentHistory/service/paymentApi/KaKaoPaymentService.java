package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.redis.PaymentRedisDto;
import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.KakaoPayApproveResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.KakaoPayReadyResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentSuccessResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PreparePaymentResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentAgreement;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.KakaoPayException;
import com.yanolja.scbj.domain.paymentHistory.exception.PaymentHistoryNotFoundException;
import com.yanolja.scbj.domain.paymentHistory.exception.ProductNotForSaleException;
import com.yanolja.scbj.domain.paymentHistory.exception.ProductOutOfStockException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.paymentHistory.util.PaymentAgreementMapper;
import com.yanolja.scbj.domain.paymentHistory.util.PaymentHistoryMapper;
import com.yanolja.scbj.domain.paymentHistory.util.PaymentRedisMapper;
import com.yanolja.scbj.domain.paymentHistory.dto.response.KakaoPayRefundResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.config.RetryConfig;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.exception.InternalServerException;
import com.yanolja.scbj.global.util.SecurityUtil;
import com.yanolja.scbj.global.util.TimeValidator;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service(value = "kakaoPaymentService")
@RequiredArgsConstructor
public class KaKaoPaymentService implements PaymentApiService {

    private final String REDIS_CACHE_KEY_PREFIX = "kakaoPay:";
    private final String REDIS_LOCK_KEY_PREFIX = "redis:lock:productId:";
    private final int FIXED_QUANTITY = 1;
    private final int TAX_FREE_AMOUNT = 0;
    private final int REDIS_CACHE_TIME_OUT = 16;
    private final String KAKAO_BASE_URL = "https://kapi.kakao.com/v1/payment";
    private final int OUT_OF_STOCK = 0;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final AlarmService alarmService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    private final RedissonClient redissonClient;
    private final SecurityUtil securityUtil;
    @Value("${server.url}")
    private String BASE_URL;
    private HttpHeaders headers;

    @Value("${kakao-api.api-key}")
    private String kakaoPayKey;

    @PostConstruct
    protected void init() {
        headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + kakaoPayKey);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    @Retryable(
        retryFor = KakaoPayException.class,
        maxAttempts = RetryConfig.MAX_ATTEMPTS,
        backoff = @Backoff(delay = RetryConfig.MAX_DELAY)
    )
    public PreparePaymentResponse preparePayment(Long productId,
        PaymentReadyRequest paymentReadyRequest) {

        Long currentMemberId = securityUtil.getCurrentMemberId();

        Product targetProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));

        checkProductNotForSale(targetProduct, currentMemberId);

        Hotel targetHotel = targetProduct.getReservation().getHotel();

        int price = getPrice(targetProduct, targetProduct.getReservation());

        String productName = createProductName(targetHotel);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(
            createPrepareParams(productId, currentMemberId, productName, price), headers);
        try {
            ResponseEntity<KakaoPayReadyResponse> response = restTemplate.postForEntity(
                new URI(KAKAO_BASE_URL + "/ready"), body, KakaoPayReadyResponse.class);

            checkStatus(response.getStatusCode(), ErrorCode.KAKAO_PAY_READY_FAIL);
            KakaoPayReadyResponse kakaoPayReadyResponse = response.getBody();

            PaymentRedisDto paymentInfo = PaymentRedisMapper.toRedisDto(productId,
                kakaoPayReadyResponse, price, paymentReadyRequest, productName);

            String key = REDIS_CACHE_KEY_PREFIX + currentMemberId;
            redisTemplate.opsForValue()
                .set(key, paymentInfo, Duration.ofMinutes(REDIS_CACHE_TIME_OUT));

            return PreparePaymentResponse.builder()
                .url(kakaoPayReadyResponse.redirectPcUrl())
                .build();
        } catch (URISyntaxException | NullPointerException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_READY_FAIL);
        }
    }

    private void checkProductNotForSale(Product product, long memberId) {
        if (product.getMember().getId().equals(memberId)) {
            throw new ProductNotForSaleException(ErrorCode.PRODUCT_NOT_FOR_SALE);
        }
    }

    private int getPrice(Product product, Reservation reservation) {
        int price = product.getFirstPrice();
        if (TimeValidator.isOverSecondGrantPeriod(product,
            reservation.getStartDate())) {
            price = product.getSecondPrice();
        }
        return price;
    }

    private String createProductName(Hotel hotel) {
        return hotel.getHotelName() + " " + hotel.getRoom().getRoomName();
    }

    private MultiValueMap<String, Object> createPrepareParams(Long productId, Long memberId,
        String productName, int price) {

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", productId);
        params.add("partner_user_id", memberId);
        params.add("item_name", productName);
        params.add("quantity", FIXED_QUANTITY);
        params.add("total_amount", price);
        params.add("tax_free_amount", TAX_FREE_AMOUNT);
        params.add("approval_url", BASE_URL + productId + "/ready");
        params.add("cancel_url", BASE_URL + productId + "/cancel");
        params.add("fail_url", BASE_URL + "/pay-fail");

        return params;
    }

    @Override
    @Transactional
    public PaymentSuccessResponse approvePaymentWithLock(String pgToken) {
        Long memberId = securityUtil.getCurrentMemberId();
        String key = REDIS_CACHE_KEY_PREFIX + memberId;
        PaymentRedisDto paymentInfo = getPaymentInfo(key);

        RLock lock = redissonClient.getLock(REDIS_LOCK_KEY_PREFIX + paymentInfo.productId());

        try {
            if (!lock.tryLock(500, 5_000, TimeUnit.MICROSECONDS)) {
                throw new KakaoPayException(ErrorCode.KAKAO_PAY_INFO_FAIL);
            }
            Product targetProduct = productRepository.findById(paymentInfo.productId())
                .orElseThrow();
            checkOutOfStock(targetProduct);

            return approvePayment(pgToken, memberId);
        } catch (InterruptedException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_INFO_FAIL);
        } finally {
            if (lock != null && lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
        retryFor = KakaoPayException.class,
        maxAttempts = RetryConfig.MAX_ATTEMPTS,
        backoff = @Backoff(delay = RetryConfig.MAX_DELAY)
    )
    public PaymentSuccessResponse approvePayment(String pgToken, Long memberId) {

        String key = REDIS_CACHE_KEY_PREFIX + memberId;

        PaymentRedisDto paymentInfo = getPaymentInfo(key);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(
            createApproveParams(paymentInfo, memberId, pgToken), headers);

        Product targetProduct = productRepository.findById(paymentInfo.productId())
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        checkOutOfStock(targetProduct);

        try {
            ResponseEntity<KakaoPayApproveResponse> response = restTemplate.postForEntity(
                new URI(KAKAO_BASE_URL + "/approve"), body,
                KakaoPayApproveResponse.class);

            checkStatus(response.getStatusCode(), ErrorCode.KAKAO_PAY_INFO_FAIL);

            Member buyer = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

            PaymentAgreement agreement = PaymentAgreementMapper.toPaymentAgreement(paymentInfo);

            PaymentHistory paymentHistory = PaymentHistoryMapper.toPaymentHistory(buyer,
                agreement, paymentInfo, targetProduct);

            targetProduct.sell();
            PaymentHistory savedPaymentHistory = paymentHistoryRepository.save(paymentHistory);

            alarmService.createAlarm(targetProduct.getMember().getId(), savedPaymentHistory.getId(),
                new Data("판매완료", paymentInfo.productName() + "의 판매가 완료되었어요!", LocalDateTime.now()));

            return PaymentSuccessResponse.builder()
                .paymentHistoryId(savedPaymentHistory.getId())
                .build();
        } catch (RestClientException | URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_INFO_FAIL);
        }
    }

    private MultiValueMap<String, Object> createApproveParams(PaymentRedisDto paymentInfo,
        long memberId, String pgToken) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();

        params.add("cid", "TC0ONETIME");
        params.add("tid", paymentInfo.tid());
        params.add("partner_order_id", paymentInfo.productId());
        params.add("partner_user_id", memberId);
        params.add("pg_token", pgToken);

        return params;
    }

    private void checkOutOfStock(Product product) {
        if (product.getStock() == OUT_OF_STOCK) {
            throw new ProductOutOfStockException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }
    }

    @Override
    public void refundPayment(Long paymentHistoryId) {
        PaymentHistory targetPaymentHistory = paymentHistoryRepository.findById(paymentHistoryId)
            .orElseThrow(() -> new PaymentHistoryNotFoundException(ErrorCode.KAKAO_PAY_RUFUND_FAIL));

        MultiValueMap<String, Object> body = createRefundParams(targetPaymentHistory);
        ResponseEntity<KakaoPayRefundResponse> kakaoPayRefundResponse;
        try {
            kakaoPayRefundResponse = restTemplate.postForEntity(
                new URI(KAKAO_BASE_URL + "/cancel"), body, KakaoPayRefundResponse.class);
        } catch (URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_RUFUND_FAIL);
        }

        if("CANCEL_PAYMENT".equals(kakaoPayRefundResponse.getBody().status())) {
            paymentHistoryRepository.deleteById(paymentHistoryId);
            return;
        }

        throw new KakaoPayException(ErrorCode.KAKAO_PAY_RUFUND_FAIL);
    }

    private MultiValueMap<String, Object> createRefundParams(PaymentHistory targetPaymentHistory) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", targetPaymentHistory.getTid());
        params.add("cancel_amount", targetPaymentHistory.getPrice());
        params.add("cancel_tax_free_amount", TAX_FREE_AMOUNT);

        return params;
    }


    @Override
    @Retryable(
        retryFor = KakaoPayException.class,
        maxAttempts = RetryConfig.MAX_ATTEMPTS,
        backoff = @Backoff(delay = RetryConfig.MAX_DELAY)
    )
    public void cancelPayment() {
        Long memberId = securityUtil.getCurrentMemberId();

        String key = REDIS_CACHE_KEY_PREFIX + memberId;

        PaymentRedisDto paymentInfo = getPaymentInfo(key);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(
            createCancelParams(paymentInfo), headers);

        try {
            ResponseEntity<KakaoPayApproveResponse> response = restTemplate.postForEntity(
                new URI(KAKAO_BASE_URL + "/cancel"), body, KakaoPayApproveResponse.class);

            checkStatus(response.getStatusCode(), ErrorCode.KAKAO_PAY_CANCEL_FAIL);

        } catch (URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_CANCEL_FAIL);
        } catch (Exception e) {
        }
    }

    private void checkStatus(HttpStatusCode statusCode, ErrorCode errorCode) {
        if (statusCode.equals(HttpStatus.BAD_REQUEST) || statusCode.equals(
            HttpStatus.UNAUTHORIZED) || statusCode.equals(HttpStatus.FORBIDDEN)
            || statusCode.equals(HttpStatus.NOT_FOUND)) {

            throw new InternalServerException(ErrorCode.SERVER_ERROR);
        }

        if (statusCode.equals(HttpStatus.INTERNAL_SERVER_ERROR) ||
            statusCode.equals(HttpStatus.SERVICE_UNAVAILABLE)) {

            throw new KakaoPayException(errorCode);
        }
    }

    private MultiValueMap<String, Object> createCancelParams(PaymentRedisDto paymentInfo) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", paymentInfo.tid());
        params.add("cancel_amount", paymentInfo.price());
        params.add("cancel_tax_free_amount", TAX_FREE_AMOUNT);

        return params;
    }

    private PaymentRedisDto getPaymentInfo(String key) {
        return (PaymentRedisDto) redisTemplate.opsForValue().get(key);
    }

    @Recover
    private void sendExceptionForPayFailure(KakaoPayException e) {
        throw new KakaoPayException(e.getErrorCode());
    }
}