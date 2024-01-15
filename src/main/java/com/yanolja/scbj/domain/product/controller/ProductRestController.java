package com.yanolja.scbj.domain.product.controller;

import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/products")
public class ProductRestController {

    private final ProductService productService;
    private final SecurityUtil securityUtil;

    @PostMapping("/{reservation_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<ProductPostResponse> postProduct(
        @PathVariable("reservation_id") Long reservationId,
        @Valid @RequestBody ProductPostRequest productPostRequest) {
        return ResponseDTO.res(
            productService.postProduct(securityUtil.getCurrentMemberId(), reservationId,
                productPostRequest),
            "양도글 작성을 성공했습니다.");
    }

    @GetMapping("/{product_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<ProductFindResponse> findProduct(
        @PathVariable("product_id") Long productId) {
        return ResponseDTO.res(productService.findProduct(productId), "상품 상세 조회에 성공했습니다.");
    }

    @DeleteMapping("/{product_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseDTO<Void> deleteProduct(
        @PathVariable("product_id") Long productId) {
        productService.deleteProduct(productId);
        return ResponseDTO.res("상품 삭제에 성공했습니다.");
    }

}
