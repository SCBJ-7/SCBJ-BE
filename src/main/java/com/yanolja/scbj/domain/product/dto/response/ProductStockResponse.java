package com.yanolja.scbj.domain.product.dto.response;

import lombok.Builder;

public record ProductStockResponse(
    boolean hasStock
) {

    @Builder
    public ProductStockResponse{

    }

}
