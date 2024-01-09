package com.yanolja.scbj.global.exception;

import com.yanolja.scbj.global.common.ResponseDTO;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<String>> bindException(BindException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ResponseDTO.res(e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<String>> applicationException(ApplicationException e) {
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
            .body(ResponseDTO.res(e.getMessage()));

    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<String>> expiredJWTException(ExpiredJwtException e) {
        return ResponseEntity.status(ErrorCode.EXPIRED_TOKEN.getHttpStatus())
            .body(ResponseDTO.res(ErrorCode.EXPIRED_TOKEN.getSimpleMessage()));
    }

    @ExceptionHandler({MalformedJwtException.class, SignatureException.class,
        UnsupportedJwtException.class})
    public ResponseEntity<ResponseDTO<String>> invalidJWTException(Exception e) {
        return ResponseEntity.status(ErrorCode.INVALID_TOKEN.getHttpStatus())
            .body(ResponseDTO.res(ErrorCode.INVALID_TOKEN.getSimpleMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseDTO<String>> ValidationException(MethodValidationException e) {
        return ResponseEntity.badRequest().body(ResponseDTO.res(e.getMessage()));
    }
}