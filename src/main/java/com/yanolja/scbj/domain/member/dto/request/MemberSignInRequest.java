package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.Password;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public record MemberSignInRequest(
    @NotNull
    @Email(message = "유효하지 않은 이메일 형식입니다.",
        regexp = EMAIL_REGEX)
    String email,
    @NotNull
    @Password
    String password
) {

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

    @Builder
    public MemberSignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
