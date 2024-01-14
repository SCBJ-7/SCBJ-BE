package com.yanolja.scbj.domain.payment.dto.response;

public record PaymentCancelResponse(
    String item_name,
    PaymentAmountResponse amount
) {


}
