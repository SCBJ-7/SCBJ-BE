package com.yanolja.scbj.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //JWT
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 액세스 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "만료된 액세스 토큰입니다."),
    //MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 틀렸습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 리프레쉬 토큰입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "입력된 패스워드 형식이 맞지 않습니다."),
    NOT_FOUND_YANOLJA_MEMBER(HttpStatus.NOT_FOUND,"야놀자 계정을 찾을 수 없습니다."),

    EMAIL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 서버가 연결되지 않습니다."),

    //PRODUCT
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다."),

    //PAYMENT
    PAYMENT_LOAD_FAIL(HttpStatus.BAD_REQUEST, "결제에 실패하였습니다."),
    PURCHASE_LOAD_FAIL(HttpStatus.BAD_REQUEST, "구매내역을 불러오지 못하였습니다"),
    SALE_DETAIL_LOAD_FAIL(HttpStatus.BAD_REQUEST,"판매내역을 불러오지 못하였습니다"),
    FIRST_PRICE_HIGHER(HttpStatus.BAD_REQUEST, "양도 가격이 구매가보다 높습니다. 양도 가격을 확인해주세요."),
    SECOND_PRICE_HIGHER(HttpStatus.BAD_REQUEST, "2차 양도 가격이 1차 양도 가격보다 높습니다. 2차 가격을 확인해주세요."),
    INVALID_SECOND_PRICE_PERIOD(HttpStatus.BAD_REQUEST, "2차 양도 가격 변동 시기는 체크인 기준 12시간 이상이어야 합니다."),


    //RESERVATION
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보를 찾을 수 없습니다."),

    //HOTELROOM
    REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "환불 규정을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String simpleMessage;

    ErrorCode(HttpStatus httpStatus, String simpleMessage) {
        this.httpStatus = httpStatus;
        this.simpleMessage = simpleMessage;
    }


}
