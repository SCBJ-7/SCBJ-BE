package com.yanolja.scbj.domain.alarm.repository;

import static org.junit.Assert.assertEquals;

import com.yanolja.scbj.domain.alarm.entity.Alarm;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Authority;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.alarm.dto.CheckInAlarmResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.config.AbstractContainersSupport;
import com.yanolja.scbj.global.config.QuerydslConfiguration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(QuerydslConfiguration.class)
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class AlarmRepositoryTest extends AbstractContainersSupport {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlarmRepository alarmRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    private Member member;

    private PaymentHistory paymentHistory;


    @BeforeEach
    void init() {
        member = createMember();
        RoomTheme roomTheme = createRoomTheme();
        Hotel hotel = createHotel(roomTheme, LocalTime.now());
        createHotelRoomImage(hotel);
        createHotelRoomPrice(hotel);
        YanoljaMember yanoljaMember = createYanoljaMember();
        Reservation reservation = createReservation(hotel, yanoljaMember, LocalDateTime.now().plusDays(1));
        Product product = createProduct(member, reservation);
        paymentHistory = createPaymentHistory(member, product);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("알람을 조회할 수 있다.")
    void getAlarm() {
        // given
        alarmRepository.save(Alarm.builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .member(member)
            .paymentHistory(paymentHistory)
            .build());

        // when
        List<Alarm> alarms= alarmRepository.getAllByMemberIdOrderByCreatedAtDesc(member.getId()).orElseThrow();

        // then
        assertEquals(alarms.size(),1);
    }

//    @Test
//    @DisplayName("알람이 필요한 결제내역을 조회할 수 있다.")
//    void findPurchasedHistoriesNeedForCheckInAlarm() {
//        // when
//        List<CheckInAlarmResponse> checkInAlarmResponses = paymentHistoryRepository.findPurchasedHistoriesNeedForCheckInAlarm();
//        // then
//        assertEquals(1, checkInAlarmResponses.size());
//    }

    private Member createMember() {
        Member member = Member.builder()
            .email("user@example.com")
            .password("password")
            .name("홍길동")
            .phone("010-1234-5678")
            .authority(Authority.ROLE_USER)
            .build();
        entityManager.persist(member);
        return member;
    }

    private RoomTheme createRoomTheme() {
        RoomTheme roomTheme = RoomTheme.builder()
            .parkingZone(true)
            .breakfast(true)
            .pool(true)
            .oceanView(false)
            .build();
        entityManager.persist(roomTheme);
        return roomTheme;
    }

    private Hotel createHotel(RoomTheme roomTheme, LocalTime checkIn) {
        Room room = Room.builder()
            .roomName("Deluxe Room")
            .checkIn(checkIn)
            .checkOut(LocalTime.of(11, 0))
            .bedType("Double Bed")
            .standardPeople(2)
            .maxPeople(4)
            .roomTheme(roomTheme)
            .build();

        Hotel hotel = Hotel.builder()
            .hotelName("롯데 시그니엘 호텔")
            .hotelMainAddress("Seoul")
            .hotelDetailAddress("123 Yanolja St, Gangnam-gu")
            .hotelInfoUrl("http://yanoljahotel.com")
            .room(room)
            .build();
        entityManager.persist(hotel);
        return hotel;
    }

    private HotelRoomImage createHotelRoomImage(Hotel hotel) {
        HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
            .hotel(hotel)
            .url("http://example.com/hotel-room-image.jpg")
            .build();
        entityManager.persist(hotelRoomImage);
        return hotelRoomImage;
    }

    private HotelRoomPrice createHotelRoomPrice(Hotel hotel) {
        HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
            .hotel(hotel)
            .peakPrice(200000)
            .offPeakPrice(150000)
            .build();
        entityManager.persist(hotelRoomPrice);
        return hotelRoomPrice;
    }

    private YanoljaMember createYanoljaMember() {
        YanoljaMember yanoljaMember = YanoljaMember.builder()
            .email("yanolja@example.com")
            .build();
        entityManager.persist(yanoljaMember);
        return yanoljaMember;
    }

    private Reservation createReservation(Hotel hotel, YanoljaMember yanoljaMember,
        LocalDateTime startDate) {
        Reservation reservation = Reservation.builder()
            .hotel(hotel)
            .yanoljaMember(yanoljaMember)
            .startDate(startDate)
            .endDate(LocalDateTime.now().plusDays(1))
            .build();
        entityManager.persist(reservation);
        return reservation;
    }

    private Product createProduct(Member member, Reservation reservation) {
        Product product = Product.builder()
            .reservation(reservation)
            .member(member)
            .bank("하나 은행")
            .accountNumber("123123")
            .build();
        entityManager.persist(product);
        return product;
    }

    private PaymentHistory createPaymentHistory(Member member, Product product) {
        PaymentHistory paymentHistory = PaymentHistory.builder()
            .member(member)
            .product(product)
            .customerEmail("customer@example.com")
            .customerName("고객 이름")
            .customerPhoneNumber("010-0000-0000")
            .price(20000)
            .paymentType("신용카드")
            .settlement(false)
            .productName("호텔이름 객실이름")
            .build();
        entityManager.persist(paymentHistory);
        return paymentHistory;
    }

}
