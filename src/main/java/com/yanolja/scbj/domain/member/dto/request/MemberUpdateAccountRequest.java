package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.ValidationGroups.NotBlankGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.SizeGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record MemberUpdateAccountRequest (

    @NotBlank(message = "유효하지 않은 계좌번호입니다.", groups = NotBlankGroup.class)
    @Pattern(regexp = "^[\\d]*$", groups = PatternGroup.class, message = "계좌번호는 숫자만 입력할 수 있어요.")
    @Size(min = 10, max = 25, groups = SizeGroup.class, message = "계좌번호가 정확히 입력되었는지 확인해주세요.")
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
