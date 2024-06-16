package com.yanolja.scbj.domain.like.dto.response;

import java.time.LocalDateTime;

public interface FavoritesResponse {
    Long getId();

    String getHotelName();

    String getBedType();

    String getImageUrl();

    LocalDateTime getCheckInDate();

    LocalDateTime getCheckOutDate();

    int getPrice();
}
