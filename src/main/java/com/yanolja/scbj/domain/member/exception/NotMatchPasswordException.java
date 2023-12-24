package com.yanolja.scbj.domain.member.exception;


import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class NotMatchPasswordException extends ApplicationException {


    public NotMatchPasswordException(ErrorCode errorCode) {
        super(errorCode);
    }
}
