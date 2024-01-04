package com.yanolja.scbj.domain.product.controller;

import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products")
public class ProductRestController {

    private final ProductService productService;
    private final SecurityUtil securityUtil;

    @PostMapping("/{reservationId}")
    public ResponseEntity<ResponseDTO<ProductPostResponse>> postProduct(@PathVariable Long reservationId,
        @Valid @RequestBody ProductPostRequest productPostRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ResponseDTO.res(
                productService.postProduct(securityUtil.getCurrentMemberId(), reservationId,
                    productPostRequest),
                "양도글 작성을 성공하였습니다."));
    }
}
