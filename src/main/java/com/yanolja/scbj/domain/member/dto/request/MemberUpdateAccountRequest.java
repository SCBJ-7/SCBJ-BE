package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.ValidationGroups.NotBlankGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record MemberUpdateAccountRequest (
    @NotBlank(message = "유효하지 않은 계좌번호입니다.", groups = NotBlankGroup.class)
    String accountNumber,
    @NotBlank(message = "유효하지 않은 은행입니다.", groups = NotBlankGroup.class)
    String bank
){

    @Builder
    public MemberUpdateAccountRequest(String accountNumber, String bank) {
        this.accountNumber = accountNumber;
        this.bank = bank;
    }
}
