package com.yanolja.scbj.domain.like.exception;

import com.yanolja.scbj.global.exception.ApplicationException;
import com.yanolja.scbj.global.exception.ErrorCode;

public class FavoriteDeleteException extends ApplicationException {

    public FavoriteDeleteException(ErrorCode errorCode) {
        super(errorCode);
    }
}
