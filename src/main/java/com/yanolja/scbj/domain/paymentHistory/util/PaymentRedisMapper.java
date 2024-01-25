package com.yanolja.scbj.domain.paymentHistory.util;

import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.request.redis.PaymentRedisRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.KakaoPayReadyResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentRedisMapper {

    public static PaymentRedisRequest toRedisRequest(long productId,
        KakaoPayReadyResponse kakaoPayReadyResponse, int price,
        PaymentReadyRequest paymentReadyRequest, String productName) {

        return PaymentRedisRequest.builder()
            .productId(productId)
            .tid(kakaoPayReadyResponse.tid())
            .price(price)
            .customerName(paymentReadyRequest.customerName())
            .customerEmail(paymentReadyRequest.customerEmail())
            .customerPhoneNumber(paymentReadyRequest.customerPhoneNumber())
            .isAgeOver14(paymentReadyRequest.isAgeOver14())
            .useAgree(paymentReadyRequest.useAgree())
            .cancelAndRefund(paymentReadyRequest.cancelAndRefund())
            .collectPersonalInfo(paymentReadyRequest.collectPersonalInfo())
            .thirdPartySharing(paymentReadyRequest.thirdPartySharing())
            .productName(productName)
            .build();
    }

}
