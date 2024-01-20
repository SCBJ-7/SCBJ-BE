package com.yanolja.scbj.domain.reservation.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationDtoConverter {

    private static final int RESERVATION_IMAGE = 0;
    private final ProductRepository productRepository;

    public List<ReservationFindResponse> toFindResponse(List<Reservation> reservationList) {
        List<Reservation> isNotProductList = isProduct(reservationList);
        List<ReservationFindResponse> reservationResList = new ArrayList<>();
        for (Reservation reservation : isNotProductList) {
            Hotel foundHotel = reservation.getHotel();
            Room foundRoom = reservation.getHotel().getRoom();
            List<HotelRoomImage> hotelRoomImageList = foundHotel.getHotelRoomImageList();
            List<RefundPolicy> refundPolicyList = foundHotel.getHotelRefundPolicyList();

            int remainingDay = (int) Duration.between(LocalDateTime.now(),
                reservation.getStartDate()).toDays();

            if (remainingDay >= 0) {
                int remainingTimes = (int) Duration.between(LocalTime.now(),
                    reservation.getStartDate().toLocalTime()).toHours();

                double refundPrice = reservation.getPurchasePrice();

                for (RefundPolicy refundPolicy : refundPolicyList) {
                    if (refundPolicy.getBaseDate() == remainingDay) {
                        refundPrice =
                            refundPolicy.getPercent() * 0.01 * reservation.getPurchasePrice();
                    }
                }
                ReservationFindResponse reservationFindResponse = ReservationFindResponse.builder()
                    .reservationId(reservation.getId())
                    .hotelName(foundHotel.getHotelName())
                    .imageUrl(hotelRoomImageList.get(RESERVATION_IMAGE).getUrl())
                    .roomName(foundRoom.getRoomName())
                    .startDate(reservation.getStartDate())
                    .endDate(reservation.getEndDate())
                    .refundPrice((int) refundPrice)
                    .purchasePrice(reservation.getPurchasePrice())
                    .remainingDays(remainingDay)
                    .remainingTimes(remainingTimes)
                    .build();

                reservationResList.add(reservationFindResponse);
            }
        }
        return reservationResList;
    }

    public List<Reservation> isProduct(List<Reservation> reservationList) {
        List<Reservation> nonProductList = new ArrayList<>();
        for (Reservation reservation : reservationList) {
            if(!productRepository.findByReservationId(reservation.getId()).isPresent()){
                nonProductList.add(reservation);
            }
        }
        return nonProductList;
    }
}
