package com.yanolja.scbj.global.config.jwt.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class InvalidTokenException extends ApplicationException {

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
