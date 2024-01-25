package com.yanolja.scbj.domain.paymentHistory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.redis.PaymentRedisDto;
import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.KakaoPayReadyResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PreparePaymentResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.ProductNotForSaleException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.paymentHistory.service.paymentApi.KaKaoPaymentService;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Transactional
@ExtendWith(MockitoExtension.class)
public class KakaoPaymentServiceTest {

    @InjectMocks
    private KaKaoPaymentService kaKaoPaymentService;

    @Mock
    private AlarmService alarmService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ValueOperations valueOperations;

    @Mock
    private SecurityUtil securityUtil;


    private Member member;
    private Product product;
    private PaymentHistory paymentHistory;

    @Test
    @DisplayName("cancelPayment로 취소 요청을 한다.")
    void cancelPayment_willSuccess() throws Exception {
        // given
        Member member = Member.builder().id(1L)
            .email("yang980329@naver.com").password("yang8126042").name("양유림")
            .phone("010-3996-6042").build();

        ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.OK);

        PaymentRedisDto paymentInfo = PaymentRedisDto.builder()
            .productId(1L)
            .price(10000)
            .cancelAndRefund(true)
            .isAgeOver14(true)
            .useAgree(true)
            .productName("Asdasdasd")
            .tid("Asgfasdafsfas")
            .build();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(redisTemplate.opsForValue().get(any())).willReturn(paymentInfo);
        given(restTemplate.postForEntity(any(), any(), any())).willReturn(
            responseEntity);

        // when
        kaKaoPaymentService.cancelPayment();

        // then
        verify(restTemplate, times(1)).postForEntity(any(), any(), any());
    }

    @Nested
    @DisplayName("카카오페이 결제는")
    class Context_KakaoPaymentService {

        @BeforeEach
        void init() {
            member = Member.builder().id(1L)
                .email("yang980329@naver.com").password("yang8126042").name("양유림")
                .phone("010-3996-6042").build();

            Room room = Room.builder()
                .checkIn(LocalTime.of(15, 0))
                .checkOut(LocalTime.of(11, 0))
                .build();

            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .peakPrice(50000000)
                .offPeakPrice(40000000)
                .build();

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .url("image1")
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelRoomPrice(hotelRoomPrice)
                .hotelRoomImageList(List.of(hotelRoomImage))
                .build();

            Reservation reservation = Reservation.builder()
                .hotel(hotel)
                .purchasePrice(50000000)
                .startDate(LocalDateTime.of(2024, 1, 15, 15, 0))
                .endDate(LocalDateTime.of(2024, 1, 16, 11, 0))
                .build();

            product = Product.builder()
                .reservation(reservation)
                .member(member)
                .bank("하나 은행")
                .accountNumber("123123")
                .firstPrice(30000000)
                .secondPrice(25000000)
                .secondGrantPeriod(3)
                .build();

            paymentHistory = PaymentHistory.builder()
                .id(1L)
                .productName("asdasd")
                .product(product)
                .member(member)
                .settlement(false)
                .paymentType("카카오페이")
                .build();
        }

        @Test
        @DisplayName("preparePayment로 결제를 준비한다.")
        void preparePayment_willSuccess() {
            // given
            long memberId = 2L;
            long productId = 1L;
            String tid = "tid1234";

            PaymentReadyRequest paymentReadyRequest = PaymentReadyRequest.builder()
                .customerName("양유림")
                .customerEmail("yang980329@naver.com")
                .customerPhoneNumber("010-3996-6042")
                .build();

            KakaoPayReadyResponse paymentReadyResponse = KakaoPayReadyResponse.builder()
                .tid(tid)
                .redirectPcUrl(
                    "http://3.34.147.187.nip.io/v1/products/1/payments?paymentType=kakaoPaymentService")
                .build();

            ResponseEntity responseEntity = new ResponseEntity<>(paymentReadyResponse,
                HttpStatus.OK);

            given(productRepository.findById(any(Long.TYPE))).willReturn(
                Optional.ofNullable(product));
            given(restTemplate.postForEntity(any(), any(), any())).willReturn(responseEntity);
            given(redisTemplate.opsForValue()).willReturn(valueOperations);

            // when
            PreparePaymentResponse result = kaKaoPaymentService.preparePayment(productId,
                paymentReadyRequest);

            // then
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.url()).isEqualTo(
                "http://3.34.147.187.nip.io/v1/products/1/payments?paymentType=kakaoPaymentService");
        }

        @Test
        @DisplayName("자신의 product를 결제하지 못한다.")
        void preparePayment_willNotSuccess() {
            // given
            long memberId = 1L;
            long productId = 1L;

            PaymentReadyRequest paymentReadyRequest = PaymentReadyRequest.builder()
                .customerName("양유림")
                .customerEmail("yang980329@naver.com")
                .customerPhoneNumber("010-3996-6042")
                .build();

            given(productRepository.findById(any(Long.TYPE))).willReturn(
                Optional.ofNullable(product));
            given(securityUtil.getCurrentMemberId()).willReturn(1L);

            // when, then
            assertThrows(ProductNotForSaleException.class, () -> {
                kaKaoPaymentService.preparePayment(productId, paymentReadyRequest);
            });
        }


        @Test
        @DisplayName("approvePayment로 승인 요청을 한다.")
        void approvePayment_willSuccess() throws Exception {
            // given
            ResponseEntity responseEntity = new ResponseEntity<>(HttpStatus.OK);

            PaymentRedisDto paymentInfo = PaymentRedisDto.builder()
                .productId(1L)
                .price(10000)
                .isAgeOver14(true)
                .tid("12234")
                .productName("양양")
                .useAgree(true)
                .cancelAndRefund(true)
                .build();

            given(redisTemplate.opsForValue()).willReturn(valueOperations);
            given(redisTemplate.opsForValue().get(any())).willReturn(paymentInfo);
            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
            given(restTemplate.postForEntity(any(), any(), any())).willReturn(
                responseEntity);
            given(memberRepository.findById(any())).willReturn(Optional.of(member));
            given(paymentHistoryRepository.save(any())).willReturn(paymentHistory);

            doNothing().when(alarmService)
                .createAlarm(anyLong(), anyLong(), any(Data.class));

            // when
            kaKaoPaymentService.approvePayment("pgtoken", member.getId());

            // then
            verify(alarmService, times(1))
                .createAlarm(anyLong(), anyLong(), any(Data.class));
        }

    }

}
