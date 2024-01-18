package com.yanolja.scbj.global.exception;

public class InternalServerException extends ApplicationException{

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
