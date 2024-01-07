package com.yanolja.scbj.domain.payment.dto;

import java.time.LocalDate;

public record SaleHistoryResponse(
    Long id,
    String name,
     String imageUrl,
    String roomType,
     Integer price,
     LocalDate checkInDate,
     LocalDate checkOutDate,
    String saleStatus
    ) {
}
