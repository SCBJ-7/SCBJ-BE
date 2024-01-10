package com.yanolja.scbj.domain.product.repository;

import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ProductRepositoryCustom {

    Page<ProductSearchResponse> search(Pageable pageable,
                                       ProductSearchRequest productSearchRequest);
}
