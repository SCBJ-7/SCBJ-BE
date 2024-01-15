package com.yanolja.scbj.domain.payment.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.payment.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.payment.dto.response.PaymentApproveResponse;
import com.yanolja.scbj.domain.payment.dto.response.PaymentCancelResponse;
import com.yanolja.scbj.domain.payment.dto.response.PaymentReadyResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentAgreement;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.enums.SecondTransferExistence;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.payment.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.util.TimeValidator;
import com.yanolja.scbj.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final String KEY_PREFIX = "kakaoPay";
    private final String PAYMENT_TYPE = "카카오페이";
    private final String BASE_URL = "http://localhost:8080/v1/products";
    private final int FIRST_IMAGE = 0;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final RedisTemplate<String, String> redisTemplate;

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

    @Transactional(readOnly = true)
    public PaymentPageFindResponse getPaymentPage(Long productId){
        Product targetProduct = productRepository.findProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));
        Reservation targetReservation = targetProduct.getReservation();
        Hotel targetHotel = targetReservation.getHotel();
        Room targetRoom = targetHotel.getRoom();
        HotelRoomPrice targetHotelRoomPrice = targetHotel.getHotelRoomPrice();
        List<HotelRoomImage> targetHotelRoomImageList = targetHotel.getHotelRoomImageList();
        int originalPrice = targetHotelRoomPrice.getOffPeakPrice();
        if(TimeValidator.isPeakTime(LocalDate.now())){
            originalPrice = targetHotelRoomPrice.getPeakPrice();
        }
        LocalDateTime checkInDateTime = targetReservation.getStartDate();
        LocalDateTime checkOutDateTime = targetReservation.getEndDate();
        int price = targetProduct.getFirstPrice();
        if (TimeValidator.isOverSecondGrantPeriod(targetProduct, checkInDateTime)) {
            price = targetProduct.getSecondPrice();
        }
        return PaymentPageFindResponse.builder()
            .hotelImage(targetHotelRoomImageList.get(FIRST_IMAGE).getUrl())
            .hotelName(targetHotel.getHotelName())
            .roomName(targetRoom.getRoomName())
            .standardPeople(targetRoom.getStandardPeople())
            .maxPeople(targetRoom.getMaxPeople())
            .checkInDateTime(checkInDateTime)
            .checkOutDateTime(checkOutDateTime)
            .originalPrice(originalPrice)
            .salePrice(price)
            .build();
    }

    @Transactional(readOnly = true)
    public String kakaoPayReady(Long memberId, Long productId,
        PaymentReadyRequest paymentReadyRequest) {

        Product targetProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));

        RestTemplate restTemplate = new RestTemplate();

        Hotel targetHotel = targetProduct.getReservation().getHotel();
        Reservation targetReservation = targetProduct.getReservation();

        String itemName = targetHotel.getHotelName() + " " + targetHotel.getRoom().getRoomName();

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
        params.add("item_name", itemName);
        params.add("quantity", 1);
        params.add("total_amount", price);
        params.add("tax_free_amount", 0);
        params.add("approval_url", BASE_URL + "/kakaopay-success?memberId=" + memberId);
        params.add("cancel_url", BASE_URL + "/kakaopay-cancel?memberId=" + memberId);
        params.add("fail_url", BASE_URL + "/kakaopay-fail");

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);
        try {
            PaymentReadyResponse paymentReadyResponse = restTemplate.postForObject(
                new URI("https://kapi.kakao.com/v1/payment/ready"), body,
                PaymentReadyResponse.class);

            Map<String, String> redisMap = new HashMap<>();
            redisMap.put("productId", String.valueOf(productId));
            redisMap.put("tid", paymentReadyResponse.getTid());
            redisMap.put("price", String.valueOf(price));
            redisMap.put("customerName", paymentReadyRequest.customerName());
            redisMap.put("customerEmail", paymentReadyRequest.customerEmail());
            redisMap.put("customerPhoneNumber", paymentReadyRequest.customerPhoneNumber());

            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            String key = KEY_PREFIX + memberId;
            hashOperations.putAll(key, redisMap);

            return paymentReadyResponse.getNext_redirect_pc_url();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    // 결제 승인 요청
    @Transactional
    public void KakaoPayInfo(String pgToken, Long memberId){

        RestTemplate restTemplate = new RestTemplate();

        String key = KEY_PREFIX + memberId;
        String productId = (String) redisTemplate.opsForHash().get(key,"productId");
        String customerName = (String) redisTemplate.opsForHash().get(key, "customerName");
        String customerEmail = (String) redisTemplate.opsForHash().get(key, "customerEmail");
        String customerPhoneNumber = (String) redisTemplate.opsForHash().get(key, "customerPhoneNumber");
        String price = (String) redisTemplate.opsForHash().get(key, "price");
        String tid = (String) redisTemplate.opsForHash().get(key, "tid");

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("partner_order_id", String.valueOf(productId));
        params.add("partner_user_id", String.valueOf(memberId));
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, Object>> body = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForObject(new URI(baseUrl + "/approve"), body,
                PaymentApproveResponse.class);

            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

            Product product = productRepository.findById(Long.valueOf(productId))
                .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

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

            paymentHistoryRepository.save(paymentHistory);

        } catch (RestClientException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void kakaoPayCancel(Long memberId){

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
            PaymentCancelResponse paymentCancelResponse = restTemplate.postForObject(
                new URI(baseUrl + "/cancel"), body,
                PaymentCancelResponse.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}