package com.yanolja.scbj.domain.paymentHistory.util;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentHistoryMapper {

    public static SpecificPurchasedHistoryResponse toSpecificPurchasedHistoryResponse(
        PaymentHistory paymentHistory, Hotel hotel, Room room, String checkIn, String checkOut,
        String paymentHistoryDate, int originalPrice, int remainingDays, String imageUrl) {
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
            .paymentHistoryDate(paymentHistoryDate)
            .hotelImage(imageUrl)
            .build();
    }

}
