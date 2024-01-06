package com.yanolja.scbj.domain.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;

public record PurchasedHistoryResponse(
    @NotNull
    Long id,
    LocalDateTime createdAt,
    String imageUrl,
    String name,
    String roomType,
    Integer price,
    LocalDate checkInDate,
    LocalDate checkOutDate
) {
}
