package com.yanolja.scbj.domain.product.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;

public record WeekendProductResponse(
    @NotNull
    Long id,
    String hotelName,
    String roomType,
    String imageUrl,
    int originalPrice,
    int salePrice,
    double salePercentage,
    LocalDate checkInDate,
    LocalDate checkOutDate,
    boolean isBrunchIncluded,
    boolean isPoolIncluded,
    boolean isOceanViewIncluded,
    int roomThemeCount,
    String hotelRate,
    String reviewRate
) {

    @Builder
    public WeekendProductResponse {
    }
}
