package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;

import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentApproveResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentCancelResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentReadyResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentAgreement;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.KakaoPayException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.enums.SecondTransferExistence;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


@Service(value = "kakaoPaymentService")
@RequiredArgsConstructor
public class KaKaoPaymentService implements PaymentApiService {

    private final String KEY_PREFIX = "kakaoPay";
    private final String PAYMENT_TYPE = "카카오페이";
    private final String BASE_URL = "http://localhost:8080/v1/products";
    private final String KAKAO_BASE_URL = "https://kapi.kakao.com/v1/payment";

    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

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
    @Transactional(readOnly = true)
    public String payReady(Long memberId, Long productId,
        PaymentReadyRequest paymentReadyRequest) {

        // 낙관적 락을 이용해 동시성 처리
        Product targetProduct = productRepository.findByIdWithOptimistic(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));

        Hotel targetHotel = targetProduct.getReservation().getHotel();
        Reservation targetReservation = targetProduct.getReservation();

        String productName = targetHotel.getHotelName() + " " + targetHotel.getRoom().getRoomName();

        LocalDateTime changeTime = null;
        LocalDateTime checkInDateTime = targetReservation.getStartDate();

        int price = targetProduct.getFirstPrice();
        if (targetProduct.getSecondGrantPeriod()
            != SecondTransferExistence.NOT_EXISTS.getStatus()) {
            int secondGrantPeriod = targetProduct.getSecondGrantPeriod();
            changeTime = checkInDateTime.minusHours(secondGrantPeriod);

            if (changeTime.isBefore(LocalDateTime.now())) {
                price = targetProduct.getSecondPrice();
            }
        }

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
            PaymentReadyResponse paymentReadyResponse = restTemplate.postForObject(
                new URI(KAKAO_BASE_URL + "/ready"), body,
                PaymentReadyResponse.class);

            Map<String, String> redisMap = new HashMap<>();
            redisMap.put("productId", String.valueOf(productId));
            redisMap.put("tid", paymentReadyResponse.tid());
            redisMap.put("price", String.valueOf(price));
            redisMap.put("customerName", paymentReadyRequest.customerName());
            redisMap.put("customerEmail", paymentReadyRequest.customerEmail());
            redisMap.put("customerPhoneNumber", paymentReadyRequest.customerPhoneNumber());

            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            String key = KEY_PREFIX + memberId;
            hashOperations.putAll(key, redisMap);

            return paymentReadyResponse.next_redirect_pc_url();
        } catch (URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_READY_FAIL);
        }
    }

    @Override
    @Transactional
    public void payInfo(String pgToken, Long memberId, Long productId, String tid) {

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("partner_order_id", productId);
        params.add("partner_user_id", String.valueOf(memberId));
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForObject(new URI(KAKAO_BASE_URL + "/approve"), body,
                PaymentApproveResponse.class);
        } catch (RestClientException | URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_INFO_FAIL);
        }
    }

    @Override
    public void payCancel(Long memberId) {

        RestTemplate restTemplate = new RestTemplate();

        String key = KEY_PREFIX + memberId;
        String tid = (String) redisTemplate.opsForHash().get(key, "tid");
        String price = (String) redisTemplate.opsForHash().get(key, "price");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("cancel_amount", Integer.parseInt(price));
        params.add("cancel_tax_free_amount", 0);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForObject(new URI(KAKAO_BASE_URL + "/cancel"), body,
                PaymentCancelResponse.class);
        } catch (URISyntaxException e) {
            throw new KakaoPayException(ErrorCode.KAKAO_PAY_CANCEL_FAIL);
        } catch (Exception e) {
        }
    }
}