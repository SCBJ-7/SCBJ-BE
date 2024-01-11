package com.yanolja.scbj.domain.payment.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.payment.dto.response.PaymentApproveResponse;
import com.yanolja.scbj.domain.payment.dto.response.PaymentReadyResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.enums.SecondTransferExistence;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;
    private PaymentReadyResponse paymentReadyResponse;
    private final MemberRepository memberRepository;
    private PaymentApproveResponse paymentApproveResponse;
    private final PaymentHistoryRepository paymentHistoryRepository;

    private HttpHeaders headers;
    @Value("${kakao-api.base-url}")
    private String baseUrl;
    @Value("${kakao-api.api-key}")
    private String key;

    @PostConstruct
    protected void init() {
        headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + key);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
    }

    public String kakaoPayReady(Long memberId, Long productId){

        Product targetProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));

        RestTemplate restTemplate = new RestTemplate();

        Hotel targetHotel = targetProduct.getReservation().getHotel();
        Reservation targetReservation = targetProduct.getReservation();

        String itemName = targetHotel.getHotelName() + " " + targetHotel.getRoom().getRoomName();

        LocalDateTime changeTime = null;
        LocalDateTime checkInDateTime = LocalDateTime.of(targetReservation.getStartDate(),
            targetHotel.getRoom().getCheckIn());

        int price = 0;
        if(targetProduct.getSecondGrantPeriod() != SecondTransferExistence.NOT_EXISTS.getStatus()){
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
        params.add("item_name", itemName);
        params.add("quantity", 1);
        params.add("total_amount", price);
        params.add("tax_free_amount", 0);
        params.add("approval_url", "http://localhost:8080/kakaopay-success?memberId=" + memberId + "&orderId=" + productId);
        params.add("cancel_url", "http://localhost:8080/kakaoPayCancel");
        params.add("fail_url", "http://localhost:8080/kakaoPaySuccessFail");

        // redis > (memberId + tid)

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);
        try {
            paymentReadyResponse = restTemplate.postForObject(new URI("https://kapi.kakao.com/v1/payment/ready"), body,
                PaymentReadyResponse.class);
            return paymentReadyResponse.getNext_redirect_pc_url();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    // 결제 승인 요청
    public PaymentApproveResponse KakaoPayInfo( String pgToken, Long memberId, Long orderId){

        RestTemplate restTemplate = new RestTemplate();

        // tid 조회 필요

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", paymentReadyResponse.getTid());
        params.add("partner_order_id", String.valueOf(orderId));
        params.add("partner_user_id", String.valueOf(memberId));
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);

        try {
            paymentApproveResponse = restTemplate.postForObject(new URI(baseUrl + "/approve"), body,
                PaymentApproveResponse.class);

            System.out.println(paymentApproveResponse.getAmount().getTotal());
//            PaymentHistory.builder()
//                    .member()
//                        .product()
//            paymentHistoryRepository.save()
            return paymentApproveResponse;

        } catch (RestClientException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }
}
