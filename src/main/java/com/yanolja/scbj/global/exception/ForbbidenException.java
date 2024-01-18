package com.yanolja.scbj.global.exception;

public class ForbbidenException extends ApplicationException{

    public ForbbidenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
