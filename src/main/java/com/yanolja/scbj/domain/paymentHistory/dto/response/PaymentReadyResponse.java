package com.yanolja.scbj.domain.paymentHistory.dto.response;

public record PaymentReadyResponse(
    String tid,
    String next_redirect_pc_url
) {

}
