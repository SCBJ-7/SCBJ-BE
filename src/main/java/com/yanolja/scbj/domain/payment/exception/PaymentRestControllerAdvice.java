package com.yanolja.scbj.domain.payment.exception;

import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.exception.ApplicationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PaymentRestControllerAdvice {


    @ExceptionHandler
    public ResponseEntity<String> paymentException(PaymentException exception) {
        return ResponseEntity.status(exception.getPaymentErrorCode().getHttpStatus())
            .body(exception.getMessage());
    }
}
