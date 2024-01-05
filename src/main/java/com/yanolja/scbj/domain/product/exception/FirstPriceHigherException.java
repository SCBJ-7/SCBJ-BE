package com.yanolja.scbj.domain.product.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class FirstPriceHigherException extends ApplicationException {

    public FirstPriceHigherException(ErrorCode errorCode) {
        super(errorCode);
    }

}
