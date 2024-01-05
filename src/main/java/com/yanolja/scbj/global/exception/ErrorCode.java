package com.yanolja.scbj.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 리프레쉬 토큰입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "입력된 패스워드 형식이 맞지 않습니다."),

    //PRODUCT
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다."),

    //PAYMENT
    PAYMENT_LOAD_FAIL(HttpStatus.BAD_REQUEST, "결제에 실패하였습니다."),
    PURCHASE_LOAD_FAIL(HttpStatus.BAD_REQUEST, "구매내역을 불러오지 못하였습니다");


    private final HttpStatus httpStatus;
    private final String simpleMessage;

    ErrorCode(HttpStatus httpStatus, String simpleMessage) {
        this.httpStatus = httpStatus;
        this.simpleMessage = simpleMessage;
    }


}
