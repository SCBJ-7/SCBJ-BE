package com.yanolja.scbj.domain.paymentHistory.dto.response;

import java.time.LocalDateTime;

public record SaleHistoryResponse(
    Long id,
    String name,
    String imageUrl,
    String roomType,
    Integer price,
    LocalDateTime checkInDate,
    LocalDateTime checkOutDate,
    String saleStatus
) {
}