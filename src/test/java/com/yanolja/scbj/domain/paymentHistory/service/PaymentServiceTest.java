package com.yanolja.scbj.domain.paymentHistory.service;

import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.RoomThemeRepository;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.YanoljaMemberRepository;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.transaction.annotation.Transactional;


@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private HotelRoomRepository hotelRoomRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomThemeRepository roomThemeRepository;

    @Autowired
    private YanoljaMemberRepository yanoljaMemberRepository;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Nested
    @DisplayName("결제 페이지 조회는 ")
    class Context_PaymentPageFind {

        @Test
        @DisplayName("성공 시 결제 할 상품의 정보를 반환한다.")
        void _will_success() {
            // given
            Long targetProductId = 1L;
            RoomTheme roomTheme = RoomTheme.builder()
                .id(1L)
                .build();

            Room room = Room.builder()
                .checkIn(LocalTime.now())
                .checkOut(LocalTime.now())
                .roomTheme(roomTheme)
                .build();

            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .id(1L)
                .offPeakPrice(100000)
                .peakPrice(200000)
                .build();

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .url("asdasdasdasd.jpg")
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelRoomImageList(List.of(hotelRoomImage))
                .hotelRoomPrice(hotelRoomPrice)
                .build();

            Reservation reservation = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

            Product product = Product.builder()
                .id(1L)
                .firstPrice(200000)
                .secondPrice(100000)
                .bank("국민")
                .accountNumber("12512-2131-12512")
                .secondGrantPeriod(24)
                .reservation(reservation)
                .build();

            given(productRepository.findProductById(targetProductId)).willReturn(
                Optional.of(product));

            // when
            PaymentPageFindResponse response = paymentService.getPaymentPage(targetProductId);

            // then
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.hotelName()).isEqualTo(hotel.getHotelName());
        }
    }




    @Nested
    @DisplayName("Lettuce를 사용하여 ")
    class Context_LettuceLock {

        @Test
        @DisplayName("동시성 제어를 성공하여 재고가 0이 된다.")
        void _will_success() throws Exception{
            YanoljaMember yanoljaMember = YanoljaMember.builder().id(1L)
                .email("yang980329@naver.com").build();

            yanoljaMemberRepository.save(yanoljaMember);

            Member member = Member.builder().id(1L).yanoljaMember(yanoljaMember)
                .email("yang980329@naver.com").password("yang8126042").name("양유림")
                .phone("010-3996-6042").build();

            memberService.signUp(
                new MemberSignUpRequest("yang980329@naver.com", "yang8126042", "양유림",
                    "010-3996-6042", true, true));

            RoomTheme roomTheme = RoomTheme.builder()
                .breakfast(true)
                .build();

            roomThemeRepository.save(roomTheme);

            Room room = Room.builder()
                .roomName("페밀리")
                .checkIn(LocalTime.now())
                .checkOut(LocalTime.now())
                .bedType("싱글")
                .standardPeople(2)
                .maxPeople(4)
                .roomTheme(roomTheme)
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .hotelName("테스트 호텔")
                .hotelMainAddress("서울")
                .hotelDetailAddress("서울광역시 강남구")
                .hotelInfoUrl("vasnoanwfowiamsfokm.jpg")
                .room(room)
                .build();

            hotelRoomRepository.save(hotel);

            Reservation reservation = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .purchasePrice(2500000)
                .yanoljaMember(yanoljaMember)
                .build();

            reservationRepository.save(reservation);


            String REDIS_CACHE_KEY_PREFIX = "kakaoPay:memberId";
            String key = REDIS_CACHE_KEY_PREFIX + member.getId();

            Map<String, String> map = new HashMap<>();
            map.put("productId", "1");
            map.put("customerName", "asdasd");
            map.put("customerEmail", "email.com");
            map.put("customerPhoneNumber", "gasdas");
            map.put("price", "3400");
            map.put("tid", "gasda");

            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(key, map);

            Product product = Product.builder()
                .reservation(reservation)
                .member(member)
                .bank("하나 은행")
                .accountNumber("123123")
                .firstPrice(30000000)
                .secondPrice(25000000)
                .secondGrantPeriod(3)
                .build();

            productRepository.save(product);

            System.err.println("================================================================================================");
            System.err.println("================================================================================================");
            // 동시성 테스트 시작 ====================================================================

            int threadCount = 3;
            ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                executorService.submit(() ->{
                    try {
                        paymentService.stockLock("asg", 1L);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            Product getProduct = productRepository.findById(1L).get();
            Assertions.assertThat(getProduct.getStock()).isEqualTo(0);

        }
    }
}