package com.yanolja.scbj.domain.product.dto.response;

import lombok.Builder;

public record ProductPostResponse(
    Long productId
) {

    @Builder
    public ProductPostResponse {
    }
}
