package com.yanolja.scbj.domain.product.controller;

import com.yanolja.scbj.domain.product.dto.ProductFindResponse;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductRestController {

    private final ProductService productService;

    @GetMapping("/{product_id}")
    public ResponseEntity<ResponseDTO<ProductFindResponse>> findProduct(
        @PathVariable("product_id") Long productId) {
        return new ResponseEntity<>(ResponseDTO.res(productService.findProduct(productId),
            "상품 상세 조회에 성공했습니다."), HttpStatus.OK);
    }

}
