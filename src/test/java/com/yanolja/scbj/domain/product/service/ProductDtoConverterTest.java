package com.yanolja.scbj.domain.product.service;

import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductDtoConverterTest {

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private ProductDtoConverter productDtoConverter;

    @Nested
    @DisplayName("상품 -> 조회 dto 변환은 ")
    class Context_ProductConverter {

        @Test
        @DisplayName("결제 내역이 존재하는 경우 saleStatus가 true로 반환된다.")
        void _will_success() {
            // given
            RoomTheme roomTheme = RoomTheme.builder()
                .id(1L)
                .build();

            Room room = Room.builder()
                .checkIn(LocalTime.now())
                .checkOut(LocalTime.now())
                .roomTheme(roomTheme)
                .build();

            HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
                .id(1L)
                .offPeakPrice(100000)
                .peakPrice(200000)
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelRoomImageList(List.of(HotelRoomImage.builder().build()))
                .hotelRoomPrice(hotelRoomPrice)
                .build();

            Reservation reservation = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

            PaymentHistory paymentHistory = PaymentHistory.builder()
                .id(1L)
                .price(100000)
                .customerName("tester")
                .customerEmail("qwe@nav.com")
                .customerPhoneNumber("010-1122-3344")
                .paymentType("카카오페이")
                .settlement(true)
                .build();

            Member member = Member.builder()
                .id(1L)
                .build();

            Product product = Product.builder()
                .id(1L)
                .member(member)
                .firstPrice(200000)
                .secondPrice(100000)
                .bank("국민")
                .accountNumber("12512-2131-12512")
                .secondGrantPeriod(24)
                .reservation(reservation)
                .paymentHistory(paymentHistory)
                .build();

            given(securityUtil.isUserNotAuthenticated()).willReturn(false);
            given(securityUtil.getCurrentMemberId()).willReturn(1L);

            // when
            ProductFindResponse response = productDtoConverter.toFindResponse(product);

            // then
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.saleStatus()).isEqualTo(true);
        }
    }
}