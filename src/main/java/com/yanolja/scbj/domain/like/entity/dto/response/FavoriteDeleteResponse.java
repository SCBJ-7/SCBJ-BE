package com.yanolja.scbj.domain.like.entity.dto.response;

import lombok.Builder;

public record FavoriteDeleteResponse(
    Long productId
) {

    @Builder
    public FavoriteDeleteResponse {
    }
}
