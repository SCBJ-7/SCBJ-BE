package com.yanolja.scbj.domain.paymentHistory.dto.response;

public record PaymentApproveResponse(
    String tid,
    PaymentAmountResponse amount
) {

}