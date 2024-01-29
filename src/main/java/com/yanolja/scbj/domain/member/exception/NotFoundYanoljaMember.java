package com.yanolja.scbj.domain.member.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class NotFoundYanoljaMember extends ApplicationException {

    public NotFoundYanoljaMember(ErrorCode errorCode) {
        super(errorCode);
    }
}
