package com.yanolja.scbj.domain.reservation.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class ReservationNotFoundException extends ApplicationException {

    public ReservationNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
