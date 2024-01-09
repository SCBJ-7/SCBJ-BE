package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.ValidationGroups.NotBlankGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record MemberUpdateAccountRequest (
    @NotBlank(message = "입력한 계좌 번호가 유효하지 않습니다.", groups = NotBlankGroup.class)
    String accountNumber,
    @NotBlank(message = "입력한 은행이 유효하지 않습니다.", groups = NotBlankGroup.class)
    String bank
){

    @Builder
    public MemberUpdateAccountRequest(String accountNumber, String bank) {
        this.accountNumber = accountNumber;
        this.bank = bank;
    }
}
