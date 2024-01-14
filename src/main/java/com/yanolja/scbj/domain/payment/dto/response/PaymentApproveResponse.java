package com.yanolja.scbj.domain.payment.dto.response;

public record PaymentApproveResponse(
    String tid,
    PaymentAmountResponse amount
) {

}
