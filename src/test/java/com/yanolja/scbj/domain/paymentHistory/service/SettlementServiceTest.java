package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomImageRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomPriceRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.RoomThemeRepository;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.entity.Authority;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.repository.YanoljaMemberRepository;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.global.config.AbstractContainersSupport;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(properties = {
    "schedule.cron = 0/1 * * * * ?",
})
class SettlementServiceTest extends AbstractContainersSupport {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

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
    private HotelRoomPriceRepository hotelRoomPriceRepository;

    @Autowired
    private HotelRoomImageRepository hotelRoomImageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Nested
    @DisplayName("정산 로직은 ")
    @TestInstance(Lifecycle.PER_CLASS)
    class Context_processSettlement {

        private Product product;
        private Member member;

        @BeforeAll
        void init() {
            RoomTheme roomTheme = RoomTheme.builder()
                .breakfast(true)
                .pool(true)
                .oceanView(true)
                .parkingZone(true)
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
                .roomAllRating("4")
                .roomKindnessRating("3")
                .roomCleanlinessRating("4.5")
                .roomConvenienceRating("5")
                .roomLocationRating("4.6")
                .facilityInformation("전 객실 금연\n싱글 침대 2개")
                .build();

            Hotel hotel = Hotel.builder()
                .hotelName("테스트 호텔")
                .hotelMainAddress("서울")
                .hotelDetailAddress("서울광역시 강남구")
                .hotelInfoUrl("vasnoanwfowiamsfokm.jpg")
                .room(room)
                .hotelLevel("4.5")
                .build();

            hotelRoomRepository.save(hotel);

            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .hotel(hotel)
                .peakPrice(50000000)
                .offPeakPrice(40000000)
                .build();

            hotelRoomPriceRepository.save(hotelRoomPrice);

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .hotel(hotel)
                .url("image1")
                .build();

            hotelRoomImageRepository.save(hotelRoomImage);

            YanoljaMember yanoljaMember = YanoljaMember.builder()
                .email("test@gmail.com").build();

            yanoljaMemberRepository.save(yanoljaMember);

            Member member = Member.builder()
                .yanoljaMember(yanoljaMember)
                .email("test133@gmail.com")
                .name("test")
                .password("agansdnaskjngaksjkfdj")
                .authority(Authority.ROLE_USER)
                .phone("01013446042")
                .build();

            memberService.signUp(
                new MemberSignUpRequest(member.getEmail(), member.getPassword(), member.getName(),
                    member.getPhone(), true, true)
            );

            member = memberService.getMember(1L);

            for (int i = 0; i < 10; i++) {

                Reservation reservation = Reservation.builder()
                    .hotel(hotel)
                    .startDate(LocalDateTime.now())
                    .endDate(LocalDateTime.now())
                    .purchasePrice(2500000)
                    .yanoljaMember(yanoljaMember)
                    .build();

                reservationRepository.save(reservation);

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

                paymentHistoryRepository.save(PaymentHistory.builder()
                    .productName("asdasd")
                    .product(product)
                    .member(member)
                    .settlement(false)
                    .paymentType("카카오페이")
                    .customerEmail("asd@naver.com")
                    .customerName("Agasdasd")
                    .customerPhoneNumber("01012341122")
                    .tid("testTid")
                    .build());
            }
        }

        @Test
        @DisplayName("시간에 맞춰 스케줄링이 된다.")
        void _will_success_scheduled() throws Exception {
            // when
            Thread.sleep(3000);

            // then
            List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();

            Assertions.assertThat(paymentHistoryList.get(0).isSettlement()).isEqualTo(true);
        }

        @Test
        @DisplayName("성공 시 정산 상태가 업데이트 된다.")
        void _will_success_update() {
            // when
            long startTime = System.currentTimeMillis();
            settlementService.settlementPaymentHistorySchedule();
            long endTime = System.currentTimeMillis();

            // then
            System.err.println("시간: " + (endTime - startTime));
            List<PaymentHistory> paymentHistoryList = paymentHistoryRepository.findAll();
            Assertions.assertThat(paymentHistoryList.get(0).isSettlement()).isEqualTo(true);
        }
    }
}