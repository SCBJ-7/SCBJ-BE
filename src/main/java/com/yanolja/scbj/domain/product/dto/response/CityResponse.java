package com.yanolja.scbj.domain.product.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;

public record CityResponse(
    @NotNull
    Long id,
    String city,
    String imageUrl,
    String hotelName,
    String roomType,
    int originalPrice,
    int salePrice,
    Double salePercentage,
    LocalDateTime checkInDate,
    LocalDateTime checkOutDate,
    String hotelRate,
    String reviewRate

) {

    @Builder
    public CityResponse {
    }
}
