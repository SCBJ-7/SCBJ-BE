package com.yanolja.scbj.domain.hotelRoom.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class RefundNotFoundException extends ApplicationException {

    public RefundNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
