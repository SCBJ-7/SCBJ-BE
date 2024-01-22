package com.yanolja.scbj.global.exception;

public class IsNotYanoljaMemberException extends ApplicationException{

    public IsNotYanoljaMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
