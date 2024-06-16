package com.yanolja.scbj.domain.like.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

public interface FavoritesResponse {
    Long getId();
    String getHotelName();
    String getBedType();
    String getImageUrl();
    LocalDateTime getCheckInDate();
    LocalDateTime getCheckOutDate();
    int getPrice();
}
