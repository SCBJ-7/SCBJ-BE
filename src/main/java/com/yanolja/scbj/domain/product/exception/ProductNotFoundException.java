package com.yanolja.scbj.domain.product.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class ProductNotFoundException extends ApplicationException {

    public ProductNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }


}
