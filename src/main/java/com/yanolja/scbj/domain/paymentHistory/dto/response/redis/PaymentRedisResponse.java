package com.yanolja.scbj.domain.paymentHistory.dto.response.redis;

import lombok.Builder;

public record PaymentRedisResponse (
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
    public PaymentRedisResponse {
    }
}
