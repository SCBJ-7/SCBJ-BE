package com.yanolja.scbj.domain.member.exception;


import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class InvalidEmailAndPasswordException extends ApplicationException {

    public InvalidEmailAndPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
