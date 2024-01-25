package com.yanolja.scbj.domain.paymentHistory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoPayCancelResponse(
    @JsonProperty("item_name")
    String itemName,
    KakaoPayAmountResponse amount
) {

}
