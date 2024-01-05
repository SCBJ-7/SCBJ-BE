package com.yanolja.scbj.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

public record MemberUpdateAccountRequest (
    @NotBlank(message = "입력된 계좌번호가 유효하지 않습니다.")
    String accountNumber,
    @NotBlank(message = "입력된 은행이 유효하지 않습니다.")
    String bank
){

    @Builder
    public MemberUpdateAccountRequest(String accountNumber, String bank) {
        this.accountNumber = accountNumber;
        this.bank = bank;
    }
}
