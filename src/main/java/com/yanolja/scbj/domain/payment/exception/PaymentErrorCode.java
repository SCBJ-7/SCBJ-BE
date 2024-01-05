package com.yanolja.scbj.domain.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PaymentErrorCode {
    PAYMENT_LOAD_FAIL(HttpStatus.BAD_REQUEST, "결제에 실패하였습니다."),
    PURCHASE_LOAD_FAIL(HttpStatus.BAD_REQUEST, "구매내역을 불러오지 못하였습니다");

    private final HttpStatus httpStatus;
    private final String message;

    PaymentErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
