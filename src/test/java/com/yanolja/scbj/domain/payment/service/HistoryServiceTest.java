package com.yanolja.scbj.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.payment.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {

    @InjectMocks
    HistoryService historyService;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentHistoryDtoConverter paymentHistoryDtoConverter;

    @BeforeEach
    void setUp() {

    }

    @Nested
    @DisplayName("구매내역 조회는")
    class Context_Purchased_getProduct {

        @Test
        @DisplayName("성공시 구매내역을 반환")
        void will_success() {
            // given
            Long memberId = 1L;
            Pageable pageable = PageRequest.of(1, 10);
            PurchasedHistoryResponse response = new PurchasedHistoryResponse(
                1L, // id
                LocalDateTime.now(), // createdAt
                "www.naver.co.kr",
                "서울 호텔", // name
                "디럭스", // roomType
                1500000, // price
                LocalDate.now().plusDays(3), // checkInDate
                LocalDate.now().plusDays(5) // checkOutDate
            );
            Page<PurchasedHistoryResponse> expectedPage =
                new PageImpl<>(List.of(response), pageable, 1);

            given(paymentHistoryRepository.findPurchasedHistoriesByMemberId(memberId,
                pageable)).willReturn(expectedPage);

            // when
            Page<PurchasedHistoryResponse> result =
                historyService.getUsersPurchasedHistory(pageable, memberId);

            //then
            assertThat(result.getContent()).containsExactly(response);
            assertThat(result).isNotNull();
        }

        void will_fail() {
        }
    }


    @Nested
    @DisplayName("판매내역 조회는")
    class Context__getSaleProductHistory {


        @Test
        @DisplayName("성공시 판매내역을 반환")
        void will_success() {
            //given
            Long memberId = 1L;
            Pageable pageable = PageRequest.of(1, 10);
            SaleHistoryResponse response = new SaleHistoryResponse(
                1L,
                "롯데 시그니엘 호텔",
                "http://example.com/hotel-room-image1.jpg",
                "더블 베드",
                200000,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2),
                "판매중");

            Page<SaleHistoryResponse> expectedPage =
                new PageImpl<>(List.of(response), pageable, 1);

            given(productRepository.findSaleHistoriesByMemberId(memberId, pageable)).willReturn(
                expectedPage);

            //when
            Page<SaleHistoryResponse> result =
                productRepository.findSaleHistoriesByMemberId(memberId, pageable);

            //then
            assertThat(result.getContent()).containsExactly(response);
            assertThat(result).isNotNull();

        }
    }

    @Nested
    @DisplayName("구매내역 상세조회는")
    class Context_getSpecificPurchasedHistory {

        @Test
        @DisplayName("성공시 상세 구매내역을 반환")
        void will_success() {
            // given
            YanoljaMember yanoljaMember = YanoljaMember.builder().id(1L)
                .email("yang980329@naver.com").build();

            Member member = Member.builder().id(1L).yanoljaMember(yanoljaMember)
                .email("yang980329@naver.com").password("yang8126042").name("양유림")
                .phone("010-3996-6042").build();

            RoomTheme roomTheme = RoomTheme.builder()
                .id(1L)
                .build();

            Room room = Room.builder()
                .checkIn(LocalTime.of(15, 0))
                .checkOut(LocalTime.of(11, 0))
                .roomTheme(roomTheme)
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .build();

            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .hotel(hotel)
                .peakPrice(50000000)
                .offPeakPrice(40000000)
                .build();

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .hotel(hotel)
                .url("image1")
                .build();

            Reservation reservation = Reservation.builder()
                .hotel(hotel)
                .purchasePrice(50000000)
                .startDate(LocalDate.of(2024, 1, 15))
                .endDate(LocalDate.of(2024, 1, 16))
                .build();

            Product product = Product.builder()
                .reservation(reservation)
                .member(member)
                .bank("하나 은행")
                .accountNumber("123123")
                .firstPrice(30000000)
                .secondPrice(25000000)
                .secondGrantPeriod(3)
                .build();

            PaymentHistory paymentHistory = PaymentHistory.builder()
                .id(1L)
                .member(member)
                .product(product)
                .customerEmail("customer@example.com")
                .customerName("고객 이름")
                .customerPhoneNumber("010-0000-0000")
                .price(25000000)
                .paymentType("신용카드")
                .settlement(false)
                .build();

            SpecificPurchasedHistoryResponse specificPurchasedHistoryResponse = SpecificPurchasedHistoryResponse.builder()
                .hotelName(hotel.getHotelName())
                .roomName(room.getRoomName())
                .standardPeople(room.getStandardPeople())
                .maxPeople(room.getMaxPeople())
                .checkIn("24.01.15 (월) 15:00")
                .checkOut("24.01.16 (화) 11:00")
                .customerName(paymentHistory.getCustomerName())
                .customerPhoneNumber(paymentHistory.getCustomerPhoneNumber())
                .paymentHistoryId(paymentHistory.getId())
                .paymentType(paymentHistory.getPaymentType())
                .originalPrice(50000000)
                .price(paymentHistory.getPrice())
                .remainingDays(4)
                .paymentHistoryDate(
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd (E) ")))
                .hotelImage(hotelRoomImage.getUrl())
                .build();

            given(paymentHistoryRepository.findByIdAndMemberId(any(Long.TYPE),
                any(Long.TYPE))).willReturn(Optional.ofNullable(paymentHistory));
            given(
                paymentHistoryDtoConverter.toSpecificPurchasedHistoryResponse((any()))).willReturn(
                specificPurchasedHistoryResponse);

            // when
            SpecificPurchasedHistoryResponse result = historyService.getSpecificPurchasedHistory(
                member.getId(), paymentHistory.getId());

            // then
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.customerName())
                .isEqualTo("고객 이름");
        }
    }
}
