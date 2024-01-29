package com.yanolja.scbj.global.config.jwt.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class ExpiredTokenException extends ApplicationException {

    public ExpiredTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
