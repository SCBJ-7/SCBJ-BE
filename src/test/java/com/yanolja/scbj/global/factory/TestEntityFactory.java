package com.yanolja.scbj.global.factory;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Authority;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.LocalTime;


public class TestEntityFactory {

    private EntityManager entityManager;
    public TestEntityFactory(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void createTestEnvironment(LocalTime checkIn, LocalDateTime startDate) {
        Member member = createMember();
        RoomTheme roomTheme = createRoomTheme();
        Hotel hotel = createHotel(roomTheme, checkIn);
        createHotelRoomImage(hotel);
        createHotelRoomPrice(hotel);
        YanoljaMember yanoljaMember = createYanoljaMember();
        Reservation reservation = createReservation(hotel, yanoljaMember, startDate);
        Product product = createProduct(member, reservation);
        PaymentHistory paymentHistory = createPaymentHistory(member, product);
        entityManager.flush();

    }

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

    private Reservation createReservation(Hotel hotel, YanoljaMember yanoljaMember, LocalDateTime startDate) {
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
