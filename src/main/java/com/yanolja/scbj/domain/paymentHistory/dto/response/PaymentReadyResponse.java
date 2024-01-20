package com.yanolja.scbj.domain.paymentHistory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PaymentReadyResponse(
    String tid,
    @JsonProperty("next_redirect_pc_url")
    String redirectPcUrl
) {

}
