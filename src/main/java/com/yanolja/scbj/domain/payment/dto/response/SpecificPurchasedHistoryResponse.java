package com.yanolja.scbj.domain.payment.dto.response;

import lombok.Builder;

public record SpecificPurchasedHistoryResponse(
    String hotelName,
    String roomName,
    int standardPeople,
    int maxPeople,
    String checkIn,
    String checkOut,
    String customerName,
    String customerPhoneNumber,
    long paymentHistoryId,
    String paymentType,
    int originalPrice,
    int price,
    int remainingDays,
    String paymentHistoryDate,
    String hotelImage
) {

    @Builder
    public SpecificPurchasedHistoryResponse {
    }
}
