package com.yanolja.scbj.domain.payment.service;

import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.payment.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import java.time.LocalTime;
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


@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Nested
    @DisplayName("결제 페이지 조회는 ")
    class Context_PaymentPageFind {

        @Test
        @DisplayName("성공 시 결제 할 상품의 정보를 반환한다.")
        void _will_success() {
            // given
            Long targetProductId = 1L;
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

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .url("asdasdasdasd.jpg")
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelRoomImageList(List.of(hotelRoomImage))
                .hotelRoomPrice(hotelRoomPrice)
                .build();

            Reservation reservation = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .build();

            Product product = Product.builder()
                .id(1L)
                .firstPrice(200000)
                .secondPrice(100000)
                .bank("국민")
                .accountNumber("12512-2131-12512")
                .secondGrantPeriod(24)
                .reservation(reservation)
                .build();

            given(productRepository.findProductById(targetProductId)).willReturn(
                Optional.of(product));

            // when
            PaymentPageFindResponse response = paymentService.getPaymentPage(targetProductId);

            // then
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.hotelName()).isEqualTo(hotel.getHotelName());
        }
    }


}
