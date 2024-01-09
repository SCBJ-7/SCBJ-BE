package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.ValidationGroups.NotBlankGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record MemberEmailRequest(
    @NotBlank(groups = NotBlankGroup.class)
    @Email(message = "유효하지 않은 이메일입니다.", groups = PatternGroup.class)
    String email
){
    @Builder
    public MemberEmailRequest(String email) {
        this.email = email;
    }

}
