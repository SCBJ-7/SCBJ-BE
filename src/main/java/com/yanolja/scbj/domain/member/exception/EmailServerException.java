package com.yanolja.scbj.domain.member.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class EmailServerException extends ApplicationException {

    public EmailServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
