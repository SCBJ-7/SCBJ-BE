package com.yanolja.scbj.domain.product.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class SecondPricePeriodException extends ApplicationException {

    public SecondPricePeriodException(ErrorCode errorCode) {
        super(errorCode);
    }
}
