package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentApproveResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentReadyResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentAgreement;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.KakaoPayException;
import com.yanolja.scbj.domain.paymentHistory.exception.ProductNotForSaleException;
import com.yanolja.scbj.domain.paymentHistory.exception.ProductOutOfStockException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.config.RetryConfig;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.exception.InternalServerException;
import com.yanolja.scbj.global.util.TimeValidator;
import io.lettuce.core.RedisClient;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
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
    private final String PAYMENT_TYPE = "카카오페이";
    private final String BASE_URL = "http://localhost:8080/v1/products";
    private final String KAKAO_BASE_URL = "https://kapi.kakao.com/v1/payment";

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final RestTemplate restTemplate;
    private final RedissonClient redissonClient;


    private HttpHeaders headers;

    @Value("${kakao-api.api-key}")
    private String key;

    @PostConstruct
    protected void init() {
        headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + key);
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
    public String preparePayment(Long memberId, Long productId, PaymentReadyRequest paymentReadyRequest) {
        Product targetProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));

        if (targetProduct.getMember().getId().equals(memberId)) {
            throw new ProductNotForSaleException(ErrorCode.PRODUCT_NOT_FOR_SALE);
        }

        Hotel targetHotel = targetProduct.getReservation().getHotel();
        Reservation targetReservation = targetProduct.getReservation();

        int price = targetProduct.getFirstPrice();
        if (TimeValidator.isOverSecondGrantPeriod(targetProduct,
            targetReservation.getStartDate())) {
            price = targetProduct.getSecondPrice();
        }

        String productName = targetHotel.getHotelName() + " " + targetHotel.getRoom().getRoomName();
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", String.valueOf(productId));
        params.add("partner_user_id", String.valueOf(memberId));
        params.add("item_name", productName);
        params.add("quantity", 1);
        params.add("total_amount", price);
        params.add("tax_free_amount", 0);
        params.add("approval_url",
            BASE_URL + "/pay-success?memberId=" + memberId + "&paymentType=kakaoPaymentService");
        params.add("cancel_url",
            BASE_URL + "/pay-cancel?memberId=" + memberId + "&paymentType=kakaoPaymentService");
        params.add("fail_url", BASE_URL + "/pay-fail");

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<PaymentReadyResponse> response = restTemplate.postForEntity(
                new URI(KAKAO_BASE_URL + "/ready"), body, PaymentReadyResponse.class);

            checkStatus(response.getStatusCode(), ErrorCode.KAKAO_PAY_READY_FAIL);
            PaymentReadyResponse paymentReadyResponse = response.getBody();

            Map<String, String> redisMap = new HashMap<>();
            redisMap.put("productId", String.valueOf(productId));
            redisMap.put("tid", paymentReadyResponse.tid());
            redisMap.put("price", String.valueOf(price));
            redisMap.put("customerName", paymentReadyRequest.customerName());
            redisMap.put("customerEmail", paymentReadyRequest.customerEmail());
            redisMap.put("customerPhoneNumber", paymentReadyRequest.customerPhoneNumber());

            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            String key = REDIS_CACHE_KEY_PREFIX + memberId;
            hashOperations.putAll(key, redisMap);

            return paymentReadyResponse.next_redirect_pc_url();
        } catch (URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_READY_FAIL);
        }
    }

    @Override
    @Transactional
    public void approvePaymentWithLock(String pgToken, Long memberId) {
        String productId = (String) redisTemplate.opsForHash()
            .get(REDIS_CACHE_KEY_PREFIX + memberId, "productId");

        RLock lock = redissonClient.getLock(REDIS_LOCK_KEY_PREFIX + productId);
        Product targetProduct = productRepository.findById(Long.valueOf(productId)).orElseThrow();
        try {
            if (!lock.tryLock(500, 5_000, TimeUnit.MICROSECONDS)) {
                throw new RuntimeException();
            }
            if (targetProduct.getStock() == 0) {
                throw new ProductOutOfStockException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            approvePayment(pgToken, memberId);
        } catch (InterruptedException e) {
            throw new RuntimeException();
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
    public void approvePayment(String pgToken, Long memberId) {

        String key = REDIS_CACHE_KEY_PREFIX + memberId;
        String productId = (String) redisTemplate.opsForHash().get(key, "productId");
        String customerName = (String) redisTemplate.opsForHash().get(key, "customerName");
        String customerEmail = (String) redisTemplate.opsForHash().get(key, "customerEmail");
        String customerPhoneNumber = (String) redisTemplate.opsForHash()
            .get(key, "customerPhoneNumber");
        String price = (String) redisTemplate.opsForHash().get(key, "price");
        String tid = (String) redisTemplate.opsForHash().get(key, "tid");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("partner_order_id", productId);
        params.add("partner_user_id", String.valueOf(memberId));
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);

        Product product = productRepository.findById(Long.valueOf(productId))
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        if (product.getStock() == 0) {
            throw new ProductOutOfStockException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        try {
            ResponseEntity<PaymentApproveResponse> response = restTemplate.postForEntity(
                new URI(KAKAO_BASE_URL + "/approve"), body,
                PaymentApproveResponse.class);

            checkStatus(response.getStatusCode(), ErrorCode.KAKAO_PAY_INFO_FAIL);

            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

            PaymentAgreement agreement = PaymentAgreement.builder()
                .build();

            PaymentHistory paymentHistory = PaymentHistory.builder()
                .member(member)
                .product(product)
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhoneNumber(customerPhoneNumber)
                .paymentAgreement(agreement)
                .price(Integer.parseInt(price))
                .paymentType(PAYMENT_TYPE)
                .build();

            product.sell();
            paymentHistoryRepository.save(paymentHistory);

        } catch (RestClientException | URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_INFO_FAIL);
        }
    }

    @Override
    @Retryable(
        retryFor = KakaoPayException.class,
        maxAttempts = RetryConfig.MAX_ATTEMPTS,
        backoff = @Backoff(delay = RetryConfig.MAX_DELAY)
    )
    public void cancelPayment(Long memberId) {

        String key = REDIS_CACHE_KEY_PREFIX + memberId;
        String tid = (String) redisTemplate.opsForHash().get(key, "tid");
        String price = (String) redisTemplate.opsForHash().get(key, "price");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("cancel_amount", Integer.parseInt(price));
        params.add("cancel_tax_free_amount", 0);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<PaymentApproveResponse> response = restTemplate.postForEntity(
                new URI(KAKAO_BASE_URL + "/cancel"), body, PaymentApproveResponse.class);

            checkStatus(response.getStatusCode(), ErrorCode.KAKAO_PAY_CANCEL_FAIL);

        } catch (URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_CANCEL_FAIL);
        } catch (Exception e) {
        }
    }

    private void checkStatus(HttpStatusCode statusCode, ErrorCode errorCode){
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

    @Recover
    private void sendExceptionForPayFailure(KakaoPayException e){
        throw new KakaoPayException(e.getErrorCode());
    }

}