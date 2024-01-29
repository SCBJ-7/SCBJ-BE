package com.yanolja.scbj.domain.paymentHistory.dto.response;

import lombok.Builder;

public record PreparePaymentResponse(
    String url
) {

    @Builder
    public PreparePaymentResponse {
    }
}
