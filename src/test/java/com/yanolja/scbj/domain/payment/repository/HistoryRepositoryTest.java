package com.yanolja.scbj.domain.payment.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Authority;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.payment.dto.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HistoryRepositoryTest {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Nested
    @DisplayName("구매내역 DB는")
    class Context_purchaseHistory {

        @Test
        @DisplayName("조회 성공시 구매내역 리스트를 보여준다")
        public void testFindPurchasedHistoriesByMemberId() {
            //given
            Member member = Member.builder()
                .email("user@example.com")
                .password("password")
                .name("홍길동")
                .phone("010-1234-5678")
                .authority(Authority.ROLE_USER)
                .build();
            entityManager.persist(member);

            RoomTheme roomTheme = RoomTheme.builder()
                .parkingZone(true)
                .breakfast(true)
                .pool(true)
                .oceanView(false)
                .build();
            entityManager.persist(roomTheme);

            Room room = Room.builder()
                .roomName("Deluxe Room")
                .checkIn(LocalTime.of(14, 0))
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

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .hotel(hotel) // 이미 생성된 Hotel 인스턴스
                .url("http://example.com/hotel-room-image.jpg")
                .build();
            entityManager.persist(hotelRoomImage);


            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .hotel(hotel)
                .peakPrice(200000)
                .offPeakPrice(150000)
                .build();
            entityManager.persist(hotelRoomPrice);

            YanoljaMember yanoljaMember = YanoljaMember.builder()
                .email("yanolja@example.com")
                .build();
            entityManager.persist(yanoljaMember);

            Reservation reservation = Reservation.builder()
                .hotel(hotel)
                .yanoljaMember(yanoljaMember)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();
            entityManager.persist(reservation);

            Product product = Product.builder()
                .reservation(reservation)
                .member(member)
                .bank("하나 은행")
                .accountNumber("123123")
                .build();
            entityManager.persist(product);

            PaymentHistory paymentHistory = PaymentHistory.builder()
                .member(member)
                .product(product)
                .customerEmail("customer@example.com")
                .customerName("고객 이름")
                .customerPhoneNumber("010-0000-0000")
                .price(20000)
                .paymentType("신용카드")
                .settlement(false)
                .build();
            entityManager.persist(paymentHistory);

            paymentHistoryRepository.save(paymentHistory);

            //when
            Page<PurchasedHistoryResponse> results =
                paymentHistoryRepository.findPurchasedHistoriesByMemberId(member.getId(),
                    PageRequest.of(0, 1));

            // then
            assertThat(results).isNotNull();
            assertThat(results.getContent()).isNotNull();
            PurchasedHistoryResponse firstResult = results.getContent().get(0);
            System.out.println("firstResult = " + firstResult.toString());
            assertThat(firstResult.name()).isEqualTo("롯데 시그니엘 호텔");
            assertThat(firstResult.imageUrl()).isEqualTo("http://example.com/hotel-room-image.jpg");
            assertThat(firstResult.price()).isEqualTo(20000);
            assertThat(firstResult.checkInDate()).isEqualTo(LocalDate.now());
        }
    }

    @Nested
    @DisplayName("판매내역 조회쿼리시")
    class Context_SaleHistory {

        @Test
        @DisplayName("settleMent가 false일시 거래완료라고 표시된다")
        public void testFindPurchasedHistoriesByMemberId() {
            //given
            Member member = Member.builder()
                .email("user@example.com")
                .password("password")
                .name("홍길동")
                .phone("010-1234-5678")
                .authority(Authority.ROLE_USER)
                .build();
            entityManager.persist(member);

            RoomTheme roomTheme = RoomTheme.builder()
                .parkingZone(true)
                .breakfast(true)
                .pool(true)
                .oceanView(false)
                .build();
            entityManager.persist(roomTheme);

            Room room = Room.builder()
                .roomName("Deluxe Room")
                .checkIn(LocalTime.of(14, 0))
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

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .hotel(hotel)
                .url("http://example.com/hotel-room-image.jpg")
                .build();
            entityManager.persist(hotelRoomImage);


            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .hotel(hotel)
                .peakPrice(200000)
                .offPeakPrice(150000)
                .build();
            entityManager.persist(hotelRoomPrice);

            YanoljaMember yanoljaMember = YanoljaMember.builder()
                .email("yanolja@example.com")
                .build();
            entityManager.persist(yanoljaMember);

            Reservation reservation = Reservation.builder()
                .hotel(hotel)
                .yanoljaMember(yanoljaMember)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(1))
                .build();
            entityManager.persist(reservation);

            Product product = Product.builder()
                .reservation(reservation)
                .member(member)
                .bank("하나 은행")
                .accountNumber("123123")
                .build();
            entityManager.persist(product);

            PaymentHistory paymentHistory = PaymentHistory.builder()
                .member(member)
                .product(product)
                .customerEmail("customer@example.com")
                .customerName("고객 이름")
                .customerPhoneNumber("010-0000-0000")
                .price(20000)
                .paymentType("신용카드")
                .settlement(false)
                .build();
            entityManager.persist(paymentHistory);

            productRepository.save(product);

            //when
            Page<SaleHistoryResponse> results =
                productRepository.findSaleHistoriesByMemberId(member.getId(), PageRequest.of(0, 1));



            //then
            assertThat(results).isNotNull();
            assertThat(results.getContent()).isNotNull();
            SaleHistoryResponse firstResult = results.getContent().get(0);
            System.out.println("firstResult = " + firstResult.toString());
            assertThat(firstResult.saleStatus()).isEqualTo("거래완료");
        }
    }
}


