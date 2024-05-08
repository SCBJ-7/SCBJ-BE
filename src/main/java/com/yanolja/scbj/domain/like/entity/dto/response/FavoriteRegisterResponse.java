package com.yanolja.scbj.domain.like.entity.dto.response;

import lombok.Builder;

public record FavoriteRegisterResponse(
    Long favoriteId,
    Boolean likeStatement
) {

    @Builder
    public FavoriteRegisterResponse {
    }
}
