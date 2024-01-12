package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.Password;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import jakarta.validation.constraints.Email;
import lombok.Builder;

public record MemberUpdatePasswordRequest(
    @Email(message = "유효하지 않은 이메일입니다.",
        regexp = EMAIL_REGEX, groups = PatternGroup.class)
    String email,
    @Password(groups = PatternGroup.class)
    String password
) {

    @Builder
    public MemberUpdatePasswordRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";

}
