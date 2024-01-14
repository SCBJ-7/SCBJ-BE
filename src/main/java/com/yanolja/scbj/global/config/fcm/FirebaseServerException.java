package com.yanolja.scbj.global.config.fcm;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class FirebaseServerException extends ApplicationException {

    public FirebaseServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
