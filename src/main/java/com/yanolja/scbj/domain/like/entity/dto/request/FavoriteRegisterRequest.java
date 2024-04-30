package com.yanolja.scbj.domain.like.entity.dto.request;

import lombok.Builder;

public record FavoriteRegisterRequest(
    boolean favoriteStatement
) {

    @Builder
    public FavoriteRegisterRequest {
    }
}
