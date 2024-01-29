package com.yanolja.scbj.domain.member.exception;


import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class InvalidRefreshTokenException extends ApplicationException {

    public InvalidRefreshTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
