package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.Password;
import com.yanolja.scbj.domain.member.validation.Phone;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

public record MemberSignUpRequest(
    @NotNull
    @Email(message = "유효하지 않은 이메일 형식입니다.",
        regexp = EMAIL_REGEX)
    String email,
    @NotNull
    @Password
    String password,

    @NotNull
    @NotBlank(message = NOT_BLANK_MESSAGE)
    String name,

    @NotNull
    @Phone
    String phone,

    @AssertTrue(message = "개인 정보 처리 방침 여부는 필수입니다.")
    Boolean privacyPolicy,

    @AssertTrue(message = "이용 약관 여부는 필수입니다.")
    Boolean termOfUse
) {

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    private static final String NOT_BLANK_MESSAGE = "공백은 사용할 수 없습니다.";

    @Builder
    public MemberSignUpRequest(String email, String password, String name, String phone,
        Boolean privacyPolicy, Boolean termOfUse) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.privacyPolicy = privacyPolicy;
        this.termOfUse = termOfUse;
    }


}
