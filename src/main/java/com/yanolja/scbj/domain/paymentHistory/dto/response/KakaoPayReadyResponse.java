package com.yanolja.scbj.domain.paymentHistory.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record KakaoPayReadyResponse(
    String tid,
    @JsonProperty("next_redirect_pc_url")
    String redirectPcUrl
) {

    @Builder
    public KakaoPayReadyResponse {
    }
}
