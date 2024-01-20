package com.yanolja.scbj.domain.paymentHistory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentCancelResponse(
    @JsonProperty("item_name")
    String itemName,
    PaymentAmountResponse amount
) {

}
