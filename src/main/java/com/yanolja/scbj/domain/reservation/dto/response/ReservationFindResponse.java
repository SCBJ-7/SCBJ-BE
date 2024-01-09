package com.yanolja.scbj.domain.reservation.dto.response;

import java.time.LocalDate;
import lombok.Builder;

public record ReservationFindResponse(
    long reservationId,
    String hotelName,
    String imageUrl,
    String roomName,
    LocalDate startDate,
    LocalDate endDate,
    int refundPrice,
    int purchasePrice,
    int remainingDays,
    int remainingTimes
) {

    @Builder
    public ReservationFindResponse {
    }
}
