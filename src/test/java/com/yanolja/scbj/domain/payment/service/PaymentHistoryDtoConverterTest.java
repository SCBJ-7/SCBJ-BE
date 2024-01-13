package com.yanolja.scbj.domain.payment.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.payment.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
class PaymentHistoryDtoConverterTest {

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
                .startDate(LocalDateTime.of(2024, 1, 15,15,0))
                .endDate(LocalDateTime.of(2024, 1, 16,11,0))
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

            // when
            SpecificPurchasedHistoryResponse result = paymentHistoryDtoConverter.toSpecificPurchasedHistoryResponse(
                paymentHistory);

            // then
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.customerName())
                .isEqualTo("고객 이름");
            Assertions.assertThat(result.remainingDays())
                .isEqualTo(2);
            Assertions.assertThat(result.originalPrice())
                .isEqualTo(50000000);

        }
    }
}