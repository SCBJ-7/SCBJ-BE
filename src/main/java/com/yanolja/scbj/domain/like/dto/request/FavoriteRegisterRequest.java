package com.yanolja.scbj.domain.like.dto.request;

import lombok.Builder;

public record FavoriteRegisterRequest(
    boolean favoriteStatement
) {

    @Builder
    public FavoriteRegisterRequest {
    }
}
