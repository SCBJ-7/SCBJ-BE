package com.yanolja.scbj.domain.payment.exception;

import lombok.Getter;

@Getter
public class PaymentException extends IllegalArgumentException {
    private final PaymentErrorCode paymentErrorCode;

    public PaymentException(PaymentErrorCode paymentErrorCode) {
        super(paymentErrorCode.getMessage());
        this.paymentErrorCode = paymentErrorCode;
    }



}
