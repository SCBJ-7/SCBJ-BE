package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.Password;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import lombok.Builder;

public record MemberUpdatePasswordRequest(
    @Password(groups = PatternGroup.class)
    String password
) {

    @Builder
    public MemberUpdatePasswordRequest(String password) {
        this.password = password;
    }

}
