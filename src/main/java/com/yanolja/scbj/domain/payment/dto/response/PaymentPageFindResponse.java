package com.yanolja.scbj.domain.payment.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

public record PaymentPageFindResponse(
    String hotelImage,
    String hotelName,
    String roomName,
    int standardPeople,
    int maxPeople,
    LocalDateTime checkInDate,
    LocalDateTime checkOutDate,
    int originalPrice,
    int salePrice
) {

    @Builder
    public PaymentPageFindResponse {
    }
}
