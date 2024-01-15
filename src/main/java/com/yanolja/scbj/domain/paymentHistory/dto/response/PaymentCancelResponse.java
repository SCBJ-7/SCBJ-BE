package com.yanolja.scbj.domain.paymentHistory.dto.response;

public record PaymentCancelResponse(
    String item_name,
    PaymentAmountResponse amount
) {

}
