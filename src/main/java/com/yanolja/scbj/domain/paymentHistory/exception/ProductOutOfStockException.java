package com.yanolja.scbj.domain.paymentHistory.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class ProductOutOfStockException extends ApplicationException {
    public ProductOutOfStockException(ErrorCode errorCode) {
        super(errorCode);
    }
}
