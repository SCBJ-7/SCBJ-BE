package com.yanolja.scbj.domain.product.dto.response;

import com.yanolja.scbj.domain.product.entity.Product;
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

    public ProductPostResponse(Product product) {
        this.productId = product.getId();
    }

}
