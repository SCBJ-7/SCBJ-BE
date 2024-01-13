package com.yanolja.scbj.domain.paymentHistory.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

public record PaymentPageFindResponse(
    String hotelImage,
    String hotelName,
    String roomName,
    int standardPeople,
    int maxPeople,
    LocalDateTime checkInDateTime,
    LocalDateTime checkOutDateTime,
    int originalPrice,
    int salePrice
) {

    @Builder
    public PaymentPageFindResponse {
    }
}
