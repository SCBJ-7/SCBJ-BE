package com.yanolja.scbj.domain.reservation.service;

import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
class ReservationDtoConverterTest {

    @InjectMocks
    private ReservationDtoConverter reservationDtoConverter;

    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("예약내역 -> 조회 dto 변환은 ")
    class Context_toFindResponse_willSuccess {

        @Test
        @DisplayName("성공시 예약내역을 반환한다.")
        void toFindResponse_willSuccess() {
            // given
            Room room = Room.builder()
                .checkIn(LocalTime.now())
                .checkOut(LocalTime.now())
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelName("신라호텔")
                .hotelRoomImageList(List.of(HotelRoomImage.builder()
                    .url("image1")
                    .build()))
                .hotelRefundPolicyList(List.of(RefundPolicy.builder()
                    .baseDate(3).percent(70).build(), RefundPolicy.builder()
                    .baseDate(2).percent(50).build(), RefundPolicy.builder()
                    .baseDate(1).percent(0).build()))
                .build();

            List<Reservation> reservationList = new ArrayList<>();

            Reservation reservation1 = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .purchasePrice(5000000)
                .startDate(LocalDateTime.of(2024, 2, 16, 14,0))
                .endDate(LocalDateTime.of(2024, 2, 18, 11, 0))
                .build();

            Reservation reservation2 = Reservation.builder()
                .id(2L)
                .hotel(hotel)
                .purchasePrice(4500000)
                .startDate(LocalDateTime.of(2024, 1, 10, 15, 0))
                .endDate(LocalDateTime.of(2024, 1, 11, 11, 0))
                .build();

            reservationList.add(reservation1);
            reservationList.add(reservation2);

            // when
            List<ReservationFindResponse> reservationFindResponse = reservationDtoConverter.toFindResponse(
                reservationList);

            // then
            Assertions.assertThat(reservationFindResponse).isNotNull();
            Assertions.assertThat(reservationFindResponse.get(0).hotelName())
                .isEqualTo("신라호텔");
        }
    }

    @Nested
    @DisplayName("양도글 작성된 reservation은")
    class Context_isProduct_willSuccess {

        @Test
        @DisplayName("reservation 조회시 보이지 않는다.")
        void toFindResponse_willSuccess() {
            //given
            Room room = Room.builder()
                .checkIn(LocalTime.now())
                .checkOut(LocalTime.now())
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelName("신라호텔")
                .hotelRoomImageList(List.of(HotelRoomImage.builder()
                    .url("image1")
                    .build()))
                .hotelRefundPolicyList(List.of(RefundPolicy.builder()
                    .baseDate(3).percent(70).build(), RefundPolicy.builder()
                    .baseDate(2).percent(50).build(), RefundPolicy.builder()
                    .baseDate(1).percent(0).build()))
                .build();

            List<Reservation> reservationList = new ArrayList<>();

            Reservation reservation1 = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .purchasePrice(5000000)
                .startDate(LocalDateTime.of(2024, 2, 16, 14,0))
                .endDate(LocalDateTime.of(2024, 2, 18, 11, 0))
                .build();

            Reservation reservation2 = Reservation.builder()
                .id(2L)
                .hotel(hotel)
                .purchasePrice(4500000)
                .startDate(LocalDateTime.of(2024, 1, 10, 15, 0))
                .endDate(LocalDateTime.of(2024, 1, 11, 11, 0))
                .build();

            reservationList.add(reservation1);
            reservationList.add(reservation2);

            Product product = Product.builder()
                .id(1L)
                .reservation(reservation1)
                .build();

            given(productRepository.findByReservationId(reservation1.getId())).willReturn(
                java.util.Optional.ofNullable(product));

            //when
            List<Reservation> result = reservationDtoConverter.isProduct(reservationList);

            //then
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result.get(0).getId()).isEqualTo(2L);
        }
    }

}