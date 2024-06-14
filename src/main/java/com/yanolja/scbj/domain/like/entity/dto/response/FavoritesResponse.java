package com.yanolja.scbj.domain.like.entity.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

public record FavoritesResponse(
    Long id,
    String hotelName,
    String roomType,
    String imageUrl,
    LocalDateTime checkInDate,
    LocalDateTime checkOutDate,
    int price
) {

    @Builder
    public FavoritesResponse {
    }
}
