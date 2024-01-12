package com.yanolja.scbj.domain.payment.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.payment.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PaymentHistoryDtoConverter {

    public SpecificPurchasedHistoryResponse toSpecificPurchasedHistoryResponse(
        PaymentHistory paymentHistory) {

        Product product = paymentHistory.getProduct();
        Reservation reservation = product.getReservation();
        Hotel hotel = product.getReservation().getHotel();
        Room room = hotel.getRoom();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy.MM.dd (E) ");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String checkIn =
            reservation.getStartDate().format(dateFormatter) + hotel.getRoom().getCheckIn()
                .format(timeFormatter);
        String checkOut =
            reservation.getEndDate().format(dateFormatter) + hotel.getRoom().getCheckOut()
                .format(timeFormatter);

        int originalPrice = hotel.getHotelRoomPrice().getOffPeakPrice();

        if (TimeValidator.isPeakTime(LocalDate.now())) {
            originalPrice = hotel.getHotelRoomPrice().getPeakPrice();
        }

        int remainingDays = (int) ChronoUnit.DAYS.between(LocalDate.now(),
            reservation.getStartDate());

        return SpecificPurchasedHistoryResponse.builder()
            .hotelName(hotel.getHotelName())
            .roomName(room.getRoomName())
            .standardPeople(room.getStandardPeople())
            .maxPeople(room.getMaxPeople())
            .checkIn(checkIn)
            .checkOut(checkOut)
            .customerName(paymentHistory.getCustomerName())
            .customerPhoneNumber(paymentHistory.getCustomerPhoneNumber())
            .paymentHistoryId(paymentHistory.getId())
            .originalPrice(originalPrice)
            .price(paymentHistory.getPrice())
            .remainingDays(remainingDays)
            .paymentHistoryDate(Optional.ofNullable(paymentHistory.getCreatedAt())
                .map(date -> date.format(dateFormatter))
                .orElse(null))
            .hotelImage(hotel.getHotelRoomImageList().isEmpty() ? null : hotel.getHotelRoomImageList().get(0).getUrl())
            .build();
    }
}
