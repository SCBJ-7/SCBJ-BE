package com.yanolja.scbj.domain.reservation.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class ReservationDtoConverter {

    public ReservationFindResponse toFindResponse(Reservation reservation,
        RefundPolicy refundPolicy) {

        Hotel foundHotel = reservation.getHotel();
        Room foundRoom = reservation.getHotel().getRoom();

        return ReservationFindResponse.builder()
            .hotelName(foundHotel.getHotelName())
            .roomName(foundRoom.getRoomName())
            .startDate(reservation.getStartDate())
            .endDate(reservation.getEndDate())
            .refundPrice(3) //환불정채 entity 수정 후에 변경예정
            .purchasePrice(reservation.getPurchasePrice())
            .remainingDays(
                (int) ChronoUnit.DAYS.between(LocalDate.now(), reservation.getStartDate()))
            .remainingTimes(
                LocalDateTime.now().getHour() - foundRoom.getCheckIn().getHour())
            .build();

    }
}
