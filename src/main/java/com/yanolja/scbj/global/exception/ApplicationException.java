package com.yanolja.scbj.global.exception;


import lombok.Getter;

@Getter
public abstract class ApplicationException extends RuntimeException{

    private ErrorCode errorCode;
    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getSimpleMessage());
        this.errorCode = errorCode;
    }
}
