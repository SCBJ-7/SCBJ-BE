package com.yanolja.scbj.domain.paymentHistory.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class ProductNotForSaleException extends ApplicationException {


    public ProductNotForSaleException(ErrorCode errorCode) {
        super(errorCode);
    }
}
