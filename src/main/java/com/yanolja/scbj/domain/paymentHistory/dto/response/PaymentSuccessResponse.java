package com.yanolja.scbj.domain.paymentHistory.dto.response;

import lombok.Builder;

public record PaymentSuccessResponse(
    Long paymentHistoryId
) {
    @Builder
    public PaymentSuccessResponse {
    }
}
