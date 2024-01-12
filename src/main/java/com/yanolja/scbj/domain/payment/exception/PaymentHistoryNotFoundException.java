package com.yanolja.scbj.domain.payment.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class PaymentHistoryNotFoundException extends ApplicationException {

    public PaymentHistoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}