package com.yanolja.scbj.domain.paymentHistory.dto.response;

import lombok.Data;

@Data
public class PaymentApproveResponse {
    private String tid;
    private PaymentAmountResponse amount;
}
