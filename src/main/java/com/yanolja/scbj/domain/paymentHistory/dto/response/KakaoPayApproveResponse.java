package com.yanolja.scbj.domain.paymentHistory.dto.response;

public record KakaoPayApproveResponse(
    String tid,
    KakaoPayAmountResponse amount
) {

}