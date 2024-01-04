package com.yanolja.scbj.global.common;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

@Getter
@Setter
@ToString
@Builder
public class ResponseDTO<T> {

    private String message;
    private T data;

    /**
     * HTTP 상태 코드, 데이터, 그리고 메시지를 이용하여 ResponseDTO 객체를 생성하는 메서드
     *
     * @param data       응답 데이터
     * @param message    응답 메시지
     * @param <T>        데이터의 제네릭 타입
     * @return 생성된 ResponseDTO 객체
     */
    public static <T> ResponseDTO<T> res(@Nullable T data, String message) {
        return ResponseDTO.<T>builder()
            .message(message)
            .data(data)
            .build();
    }

    /**
     * HTTP 상태 코드와 메시지를 이용하여 ResponseDTO 객체를 생성하는 메서드
     *
     * @param message    응답 메시지
     * @param <T>        데이터의 제네릭 타입
     * @return 생성된 ResponseDTO 객체
     */
    public static <T> ResponseDTO<T> res(String message) {
        return ResponseDTO.<T>builder()
            .message(message)
            .build();
    }
}
