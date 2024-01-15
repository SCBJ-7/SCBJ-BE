package com.yanolja.scbj.domain.paymentHistory.dto.response;

import lombok.Data;

@Data
public class PaymentCancelResponse {
    private String item_name;
    private PaymentAmountResponse amount;

}
