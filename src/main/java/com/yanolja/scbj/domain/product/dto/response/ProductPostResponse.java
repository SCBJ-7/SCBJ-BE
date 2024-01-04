package com.yanolja.scbj.domain.product.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductPostResponse {

    private Long productId;

    @Builder
    public ProductPostResponse(Long productId) {
        this.productId = productId;
    }

}
