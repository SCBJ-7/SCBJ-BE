package com.yanolja.scbj.domain.alarm.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class AlarmNotFoundException extends ApplicationException {

    public AlarmNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
