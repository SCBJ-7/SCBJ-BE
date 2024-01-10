package com.yanolja.scbj.domain.reservation.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReservationDtoConverter {

    public List<ReservationFindResponse> toFindResponse(List<Reservation> reservationList) {

        List<ReservationFindResponse> reservationResList = new ArrayList<>();

        for (Reservation reservation : reservationList) {
            Hotel foundHotel = reservation.getHotel();
            Room foundRoom = reservation.getHotel().getRoom();
            int remainingDays = (int) ChronoUnit.DAYS.between(LocalDate.now(),
                reservation.getStartDate());
            double refundPrice = reservation.getPurchasePrice();

            List<HotelRoomImage> hotelRoomImageList = foundHotel.getHotelRoomImageList();
            List<RefundPolicy> refundPolicyList = foundHotel.getHotelRefundPolicyList();

            for (RefundPolicy refundPolicy : refundPolicyList) {
                if (refundPolicy.getBaseDate() == remainingDays) {
                    refundPrice = refundPolicy.getPercent() * 0.01 * reservation.getPurchasePrice();
                }
            }

            ReservationFindResponse reservationFindResponse = ReservationFindResponse.builder()
                .reservationId(reservation.getId())
                .hotelName(foundHotel.getHotelName())
                .imageUrl(hotelRoomImageList.get(0).getUrl())
                .roomName(foundRoom.getRoomName())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .refundPrice((int) refundPrice)
                .purchasePrice(reservation.getPurchasePrice())
                .remainingDays(remainingDays)
                .remainingTimes(
                    LocalDateTime.now().getHour() - foundRoom.getCheckIn().getHour())
                .build();

            reservationResList.add(reservationFindResponse);
        }

        return reservationResList;
    }
}
