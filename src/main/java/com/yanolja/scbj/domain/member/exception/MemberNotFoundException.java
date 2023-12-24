package com.yanolja.scbj.domain.member.exception;


import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class MemberNotFoundException extends ApplicationException {
    public MemberNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
