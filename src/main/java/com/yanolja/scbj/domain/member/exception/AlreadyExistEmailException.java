package com.yanolja.scbj.domain.member.exception;


import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class AlreadyExistEmailException extends ApplicationException {

    public AlreadyExistEmailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
