package com.yanolja.scbj.domain.product.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.dto.ProductFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalTime;
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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDtoConverter productDtoConverter;

    @InjectMocks
    private ProductService productService;

    @Nested
    @DisplayName("상품 상세 조회는 ")
    class Context_findProduct {

        @Test
        @DisplayName("성공시 상품 정보를 반환한다.")
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
                .hotelRoomPrice(hotelRoomPrice)
                .build();

            Reservation reservation = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
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

            Product product = Product.builder()
                .id(1L)
                .firstPrice(200000)
                .secondPrice(100000)
                .bank("국민")
                .accountNumber("12512-2131-12512")
                .secondGrantPeriod(24)
                .reservation(reservation)
                .paymentHistory(paymentHistory)
                .build();

            ProductFindResponse ConverterResponse = ProductFindResponse.builder()
                .hotelName(hotel.getHotelName())
                .saleStatus(true)
                .build();

            given(productRepository.findById(any())).willReturn(Optional.of(product));
            given(productDtoConverter.toFindResponse(any())).willReturn(ConverterResponse);

            // when
            ProductFindResponse response = productService.findProduct(product.getId());

            // then
            Assertions.assertThat(response).isNotNull();
            Assertions.assertThat(response.isSaleStatus()).isEqualTo(true);
        }
    }

}