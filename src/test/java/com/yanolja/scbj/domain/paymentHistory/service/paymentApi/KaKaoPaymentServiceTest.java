package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.RoomThemeRepository;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.YanoljaMemberRepository;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentService;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.assertj.core.api.Assertions;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;



@SpringBootTest
//@Commit
//@Rollback(value = false)
class KaKaoPaymentServiceTest {


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
    private PaymentHistoryRepository paymentHistoryRepository;

    @DisplayName("비관적 락을 이용해 동시성을 제어한다")
    @Test
    void _will_success_with_pessimisticLock2() throws Exception {
        YanoljaMember yanoljaMember = YanoljaMember.builder().id(1L)
            .email("yang980329@naver.com").build();

        yanoljaMemberRepository.save(yanoljaMember);

        Member member = Member.builder().yanoljaMember(yanoljaMember)
            .id(1L)
            .email("yang980329@naver.com").password("yang8126042").name("양유림1")
            .phone("010-3996-6042").build();

        List<Member> memberList = new ArrayList<>();
        Member member1 = Member.builder().yanoljaMember(yanoljaMember)
            .id(1L)
            .email("yang980329@naver.com").password("yang8126042").name("양유림1")
            .phone("010-3996-6042").build();

        Member member2 = Member.builder().yanoljaMember(yanoljaMember)
            .id(2L)
            .email("yang0000@naver.com").password("yang8126042").name("양유림2")
            .phone("010-3996-6042").build();

        Member member3 = Member.builder().yanoljaMember(yanoljaMember)
            .id(3L)
            .email("yang12345@naver.com").password("yang8126042").name("양유림3")
            .phone("010-3996-6042").build();

        memberList.add(member1);
        memberList.add(member2);
        memberList.add(member3);

        memberService.signUp(
            new MemberSignUpRequest("yang980329@naver.com", "yang8126042", "양유림1",
                "010-3996-6042", true, true));

        memberService.signUp(
            new MemberSignUpRequest("yang0000@naver.com", "yang8126042", "양유림2",
                "010-3996-6042", true, true));

        memberService.signUp(
            new MemberSignUpRequest("yang12345@naver.com", "yang8126042", "양유림3",
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


        String key1 = "kakaoPay" + member1.getId();
        String key2 = "kakaoPay" + member2.getId();
        String key3 = "kakaoPay" + member3.getId();

        Map<String, String> map = new HashMap<>();
        map.put("productId", "1");
        map.put("customerName", "asdasd");
        map.put("customerEmail", "email.com");
        map.put("customerPhoneNumber", "gasdas");
        map.put("price", "3400");
        map.put("tid", "gasda");

        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        hashOperations.putAll(key1, map);
        hashOperations.putAll(key2, map);
        hashOperations.putAll(key3, map);

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

        // 동시성 테스트 시작 ====================================================================

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        List<Exception> exceptionList = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            executorService.submit(() ->{
                try {
                    paymentService.orderProductWithLock("asg", memberList.get(finalI).getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    exceptionList.add(e);
                } finally {
                    latch.countDown();

                }
            });
        }


        latch.await();
        Assertions.assertThat(exceptionList.get(0) instanceof PessimisticLockingFailureException);

    }

}
