package com.yanolja.scbj.domain.reservation.service;

import static org.junit.jupiter.api.Assertions.*;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
class ReservationDtoConverterTest {

    @InjectMocks
    private ReservationDtoConverter reservationDtoConverter;

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
                .build();

            Reservation reservation = Reservation.builder()
                .hotel(hotel)
                .purchasePrice(5000000)
                .startDate(LocalDate.now()).
                endDate(LocalDate.now())
                .build();

            RefundPolicy refundPolicy = RefundPolicy.builder().hotel(hotel)
                .baseDate(LocalDate.now()).percent(30).build();

            // when
            ReservationFindResponse reservationFindResponse = reservationDtoConverter.toFindResponse(
                reservation, refundPolicy);

            // then
            Assertions.assertThat(reservationFindResponse).isNotNull();
            Assertions.assertThat(reservationFindResponse.getPurchasePrice()).isEqualTo(5000000);
        }
    }

}