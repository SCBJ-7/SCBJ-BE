package com.yanolja.scbj.domain.paymentHistory.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class KakaoPayException extends ApplicationException {

    public KakaoPayException(ErrorCode errorCode) {
        super(errorCode);
    }
}
