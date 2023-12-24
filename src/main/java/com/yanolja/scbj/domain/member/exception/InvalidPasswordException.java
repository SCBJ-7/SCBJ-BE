package com.yanolja.scbj.domain.member.exception;


import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class InvalidPasswordException extends ApplicationException {

    public InvalidPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
