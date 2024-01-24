package com.yanolja.scbj.domain.product.util;

import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.entity.ProductAgreement;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductMapper {

    public static ProductAgreement toProductAgreement(ProductPostRequest productPostRequest) {
        return ProductAgreement.builder()
            .standardTimeSellingPolicy(productPostRequest.standardTimeSellingPolicy())
            .totalAmountPolicy(productPostRequest.totalAmountPolicy())
            .sellingModificationPolicy(productPostRequest.sellingModificationPolicy())
            .productAgreement(productPostRequest.productAgreement())
            .build();
    }

    public static Product toProduct(Reservation reservation, Member member,
        ProductAgreement productAgreement, ProductPostRequest productPostRequest) {
        return Product.builder()
            .reservation(reservation)
            .member(member)
            .productAgreement(productAgreement)
            .firstPrice(productPostRequest.firstPrice())
            .secondPrice(productPostRequest.secondPrice())
            .bank(productPostRequest.bank())
            .accountNumber(productPostRequest.accountNumber())
            .secondGrantPeriod(productPostRequest.secondGrantPeriod())
            .build();
    }

    public static ProductPostResponse toProductPostResponse(Product savedProduct) {
        return ProductPostResponse.builder().productId(savedProduct.getId()).build();
    }

}
