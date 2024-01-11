package com.yanolja.scbj.domain.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomImageRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomPriceRepository;
import com.yanolja.scbj.domain.hotelRoom.repository.HotelRoomRepository;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.payment.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
class PaymentHistoryDtoConverterTest {

    @InjectMocks
    HistoryService historyService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private HotelRoomRepository hotelRoomRepository;

    @Mock
    private HotelRoomPriceRepository hotelRoomPriceRepository;

    @Mock
    private HotelRoomImageRepository hotelRoomImageRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;


    @InjectMocks
    private PaymentHistoryDtoConverter paymentHistoryDtoConverter;


    @Nested
    @DisplayName("상세 구매 내역 -> 조회 dto 변환은")
    class Context_getSpecificPurchasedHistory {

        @Test
        @DisplayName("성공시 상세 구매내역을 반환한다.")
        void will_success() {
            // given

            Member member = Member.builder().id(1L)
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


//            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy.MM.dd (E) ");
//            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

//            SpecificPurchasedHistoryResponse specificPurchasedHistoryResponse = SpecificPurchasedHistoryResponse.builder()
//                .hotelName(hotel.getHotelName())
//                .roomName(room.getRoomName())
//                .standardPeople(room.getStandardPeople())
//                .maxPeople(room.getMaxPeople())
//                .checkIn(
//                    reservation.getStartDate().format(dateFormatter) + hotel.getRoom().getCheckIn()
//                        .format(timeFormatter))
//                .checkOut(
//                    reservation.getEndDate().format(dateFormatter) + hotel.getRoom().getCheckOut()
//                        .format(timeFormatter))
//                .customerName(paymentHistory.getCustomerName())
//                .customerPhoneNumber(paymentHistory.getCustomerPhoneNumber())
//                .paymentHistoryId(paymentHistory.getId())
//                .paymentType(paymentHistory.getPaymentType())
//                .originalPrice(hotel.getHotelRoomPrice().getPeakPrice())
//                .price(paymentHistory.getPrice())
//                .remainingDays((int) ChronoUnit.DAYS.between(LocalDate.now(),
//                    reservation.getStartDate()))
//                .paymentHistoryDate(
//                    LocalDate.now().format(DateTimeFormatter.ofPattern("yy.MM.dd (E) ")))
//                .hotelImage(hotelRoomImage.getUrl())
//                .build();


            // when

            SpecificPurchasedHistoryResponse result = paymentHistoryDtoConverter.toSpecificPurchasedHistoryResponse(
                paymentHistory);

            // then
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.customerName())
                .isEqualTo("고객 이름");
            Assertions.assertThat(result.checkIn())
                .isEqualTo("24.01.15 (월) 15:00");
            Assertions.assertThat(result.checkOut())
                .isEqualTo("24.01.16 (화) 11:00");
            Assertions.assertThat(result.remainingDays())
                .isEqualTo(4);
            Assertions.assertThat(result.originalPrice())
                .isEqualTo(50000000);

        }
    }

}