package com.yanolja.scbj.domain.product.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class SecondPriceHigherException extends ApplicationException {

    public SecondPriceHigherException(ErrorCode errorCode) {
        super(errorCode);
    }
}
