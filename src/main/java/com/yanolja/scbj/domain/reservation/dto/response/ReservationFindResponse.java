package com.yanolja.scbj.domain.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

public record ReservationFindResponse(
    long reservationId,
    String hotelName,
    String imageUrl,
    String roomName,
    LocalDateTime startDate,
    LocalDateTime endDate,
    int refundPrice,
    int purchasePrice,
    int remainingDays,
    int remainingTimes
) {

    @Builder
    public ReservationFindResponse {
    }
}
