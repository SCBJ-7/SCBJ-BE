package com.yanolja.scbj.domain.payment.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

public record PurchasedHistoryResponse(
    Long id,
    LocalDateTime createdAt,
    String name,
    String roomType,
    Integer price,
    LocalDate checkInDate,
    LocalDate checkOutDate
) {
}
