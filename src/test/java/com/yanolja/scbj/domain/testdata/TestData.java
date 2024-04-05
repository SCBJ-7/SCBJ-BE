package com.yanolja.scbj.domain.testdata;

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
import java.time.LocalDate;
import java.time.LocalTime;

public class TestData {

    public static Member createMember(String email, String phoneNumber) {
        return Member.builder()
            .email(email)
            .password("password")
            .name("홍길동")
            .phone(phoneNumber)
            .authority(Authority.ROLE_USER)
            .build();
    }

    public static RoomTheme createRoomTheme(Boolean parking, Boolean pool) {
        return RoomTheme.builder()
            .parkingZone(parking)
            .breakfast(false)
            .pool(pool)
            .oceanView(false)
            .build();
    }

    public static Hotel createHotel(RoomTheme roomTheme, String hotelAddress, Integer maxPeople) {
        Room room = Room.builder()
            .roomName("Deluxe Room")
            .checkIn(LocalTime.of(14, 0))
            .checkOut(LocalTime.of(11, 0))
            .bedType("Double Bed")
            .standardPeople(2)
            .maxPeople(maxPeople)
            .roomTheme(roomTheme)
            .roomAllRating("4")
            .roomKindnessRating("3")
            .roomCleanlinessRating("4.5")
            .roomConvenienceRating("5")
            .roomLocationRating("4.6")
            .build();

        return Hotel.builder()
            .hotelName("롯데 시그니엘 호텔")
            .hotelMainAddress(hotelAddress)
            .hotelDetailAddress("123 Yanolja St, Gangnam-gu")
            .hotelInfoUrl("http://yanoljahotel.com")
            .room(room)
            .hotelLevel("4.2")
            .build();
    }

    public static HotelRoomImage createHotelRoomImage(Hotel hotel) {
        return HotelRoomImage.builder()
            .hotel(hotel)
            .url("http://example.com/hotel-room-image.jpg")
            .build();
    }

    public static YanoljaMember createYanoljaMember(String email) {
        return YanoljaMember.builder()
            .email(email)
            .build();
    }

    public static Reservation createReservation(Hotel hotel, YanoljaMember yanoljaMember,
        LocalDate checkIn, LocalDate checkOut,
        int purchasePrice) {
        return Reservation.builder()
            .hotel(hotel)
            .yanoljaMember(yanoljaMember)
            .startDate(checkIn.atStartOfDay())
            .endDate(checkOut.atStartOfDay())
            .purchasePrice(purchasePrice)
            .build();
    }

    public static Product createProduct(Member member, Reservation reservation, int firstPrice,
        int secondPrice, int time) {
        return Product.builder()
            .reservation(reservation)
            .member(member)
            .bank("하나 은행")
            .accountNumber("123123")
            .firstPrice(firstPrice)
            .secondPrice(secondPrice)
            .secondGrantPeriod(time)
            .build();
    }

    public static HotelRoomPrice createHotelRoomPrice(Hotel hotel, int peakPrice,
        int offPeakPrice) {
        return HotelRoomPrice.builder()
            .hotel(hotel)
            .peakPrice(peakPrice)
            .offPeakPrice(offPeakPrice)
            .build();
    }

    public static PaymentHistory createPaymentHistory(Product product) {
        return PaymentHistory.builder()
            .product(product)
            .price(10000) // 예시 가격
            .customerName("홍길동")
            .customerEmail("hong.gildong@example.com")
            .customerPhoneNumber("010-1234-5678")
            .paymentType("신용카드")
            .settlement(true)
            .productName("호텔이름 객실이름")
            .build();
    }
}
