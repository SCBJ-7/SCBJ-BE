package com.yanolja.scbj.domain.paymentHistory.dto.request.redis;

import lombok.Builder;

public record PaymentRedisRequest (
    Long productId,
    String tid,
    int price,
    String customerName,
    String customerEmail,
    String customerPhoneNumber,
    boolean isAgeOver14,
    boolean useAgree,
    boolean cancelAndRefund,
    boolean collectPersonalInfo,
    boolean thirdPartySharing,
    String productName
) {

    @Builder
    public PaymentRedisRequest {
    }
}
