package com.yanolja.scbj.domain.product.dto.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

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
    LocalDateTime checkOutDate

) {

    @Builder
    public CityResponse {
    }
}
