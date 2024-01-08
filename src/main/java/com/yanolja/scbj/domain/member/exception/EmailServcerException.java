package com.yanolja.scbj.domain.member.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class EmailServcerException extends ApplicationException {

    public EmailServcerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
