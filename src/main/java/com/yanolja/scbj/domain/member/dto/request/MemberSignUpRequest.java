package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.Password;
import com.yanolja.scbj.domain.member.validation.Phone;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.NotBlankGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.SizeGroup;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record MemberSignUpRequest(
    @NotBlank(groups = NotBlankGroup.class)
    @Email(message = "유효하지 않은 이메일 형식입니다.",
        regexp = EMAIL_REGEX, groups = PatternGroup.class)
    String email,
    @Password(groups = PatternGroup.class)
    String password,

    @NotBlank(groups = NotBlankGroup.class)
    @Pattern(regexp = "[^0-9]*", message = "이름에 숫자는 입력할 수 없습니다.", groups = PatternGroup.class)
    @Size(min = 2, max = 20, message = "이름의 길이는 2 ~ 20자여야 합니다.", groups = SizeGroup.class)
    String name,

    @Phone(groups = PatternGroup.class)
    String phone,

    @AssertTrue(message = "개인 정보 처리 방침 여부는 필수입니다.", groups = PatternGroup.class)
    Boolean privacyPolicy,

    @AssertTrue(message = "이용 약관 여부는 필수입니다.", groups = PatternGroup.class)
    Boolean termOfUse
) {

    private static final String EMAIL_REGEX = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
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
