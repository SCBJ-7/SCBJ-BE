package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.product.dto.ProductFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDtoConverter productDtoConverter;

    @Transactional(readOnly = true)
    public ProductFindResponse findProduct(Long productId) {
        Product foundProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        return productDtoConverter.toFindResponse(foundProduct);
    }
}
