package com.yanolja.scbj.domain.payment.dto.response;

import lombok.Data;

@Data
public class PaymentApproveResponse {
    private String tid;
    private PaymentAmountResponse amount;
}
