package com.yanolja.scbj.domain.paymentHistory.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Authority;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.PaymentHistoryNotFoundException;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.config.QuerydslConfiguration;
import com.yanolja.scbj.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import(QuerydslConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class HistoryRepositoryTest {

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

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

    private Hotel createHotel(RoomTheme roomTheme) {
        Room room = Room.builder()
            .roomName("Deluxe Room")
            .checkIn(LocalTime.of(14, 0))
            .checkOut(LocalTime.of(11, 0))
            .bedType("Double Bed")
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
            .hotelName("롯데 시그니엘 호텔")
            .hotelMainAddress("Seoul")
            .hotelDetailAddress("123 Yanolja St, Gangnam-gu")
            .hotelInfoUrl("http://yanoljahotel.com")
            .hotelLevel("4.4")
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

    private Reservation createReservation(Hotel hotel, YanoljaMember yanoljaMember) {
        Reservation reservation = Reservation.builder()
            .hotel(hotel)
            .yanoljaMember(yanoljaMember)
            .startDate(LocalDateTime.now())
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


    @Nested
    @DisplayName("구매내역 DB는")
    class Context_purchaseHistory {

        @Test
        @DisplayName("조회 성공시 구매내역 리스트를 보여준다")
        public void testFindPurchasedHistoriesByMemberId() {
            //given
            Member member = createMember();
            RoomTheme roomTheme = createRoomTheme();
            Hotel hotel = createHotel(roomTheme);
            createHotelRoomImage(hotel);
            createHotelRoomPrice(hotel);
            YanoljaMember yanoljaMember = createYanoljaMember();
            Reservation reservation = createReservation(hotel, yanoljaMember);
            Product product = createProduct(member, reservation);
            PaymentHistory paymentHistory = createPaymentHistory(member, product);
            paymentHistoryRepository.save(paymentHistory);

            //when
            List<PurchasedHistoryResponse> results =
                paymentHistoryRepository.findPurchasedHistoriesByMemberId(member.getId());

            // then
            assertThat(results).isNotNull();
            assertThat(results).isNotNull();
            PurchasedHistoryResponse firstResult = results.get(0);
            assertThat(firstResult.name()).isEqualTo("롯데 시그니엘 호텔");
            assertThat(firstResult.imageUrl()).isEqualTo("http://example.com/hotel-room-image.jpg");
            assertThat(firstResult.price()).isEqualTo(20000);
        }
    }

    @Nested
    @DisplayName("판매내역 조회쿼리 실행시")
    class Context_SaleHistory {

        @Test
        @DisplayName("settleMent가 false일시 거래완료라고 표시된다")
        public void testFindPurchasedHistoriesByMemberId() {
            //given
            Member member = createMember();
            RoomTheme roomTheme = createRoomTheme();
            Hotel hotel = createHotel(roomTheme);
            createHotelRoomImage(hotel);
            createHotelRoomPrice(hotel);
            YanoljaMember yanoljaMember = createYanoljaMember();
            Reservation reservation = createReservation(hotel, yanoljaMember);
            Product product = createProduct(member, reservation);
            PaymentHistory paymentHistory = createPaymentHistory(member, product);
            paymentHistoryRepository.save(paymentHistory);

            //when
            List<SaleHistoryResponse> results =
                productRepository.findSaleHistoriesByMemberId(member.getId());

            //then
            assertThat(results).isNotNull();
            assertThat(results).isNotNull();
            SaleHistoryResponse firstResult = results.get(0);
            assertThat(firstResult.saleStatus()).isEqualTo("거래완료");
        }
    }

    @Nested
    @DisplayName("member와 puchaseId를 통해")
    class Context_specificPurchaseHistory {

        @Test
        @DisplayName("조회 성공시 구매 내력을 확인할 수 있다.")
        public void getSpecificPurchasedHistory() {
            // given
            Member member = createMember();
            RoomTheme roomTheme = createRoomTheme();
            Hotel hotel = createHotel(roomTheme);
            createHotelRoomImage(hotel);
            createHotelRoomPrice(hotel);
            YanoljaMember yanoljaMember = createYanoljaMember();
            Reservation reservation = createReservation(hotel, yanoljaMember);
            Product product = createProduct(member, reservation);
            PaymentHistory paymentHistory = createPaymentHistory(member, product);
            paymentHistoryRepository.save(paymentHistory);

            // when
            PaymentHistory result = paymentHistoryRepository.findByIdAndMemberId(member.getId(),
                    paymentHistory.getId())
                .orElseThrow(
                    () -> new PaymentHistoryNotFoundException(ErrorCode.PURCHASE_LOAD_FAIL));

            // then
            assertThat(result).isNotNull();
            assertThat(result.getCustomerName()).isEqualTo("고객 이름");
            assertThat(result.getPaymentType()).isEqualTo("신용카드");

        }
    }

}


