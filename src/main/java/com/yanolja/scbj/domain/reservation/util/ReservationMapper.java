package com.yanolja.scbj.domain.reservation.util;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReservationMapper {

    public static ReservationFindResponse toReservationFindResponse(Reservation reservation,
        Hotel hotel, Room room, String imageUrl,
        int remainingDay, int remainingTimes, int refundPrice) {
        return ReservationFindResponse.builder()
            .reservationId(reservation.getId())
            .hotelName(hotel.getHotelName())
            .imageUrl(imageUrl)
            .roomName(room.getRoomName())
            .startDate(reservation.getStartDate())
            .endDate(reservation.getEndDate())
            .refundPrice(refundPrice)
            .purchasePrice(reservation.getPurchasePrice())
            .remainingDays(remainingDay)
            .remainingTimes(remainingTimes)
            .build();
    }
}
