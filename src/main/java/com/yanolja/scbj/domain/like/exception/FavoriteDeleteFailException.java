package com.yanolja.scbj.domain.like.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class FavoriteDeleteFailException extends ApplicationException {

    public FavoriteDeleteFailException(ErrorCode errorCode) {
        super(errorCode);
    }
}
