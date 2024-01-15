package com.yanolja.scbj.domain.paymentHistory.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class SaleHistoryNotFoundException extends ApplicationException {
    public SaleHistoryNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
