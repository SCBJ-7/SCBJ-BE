package com.yanolja.scbj.domain.paymentHistory.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.yanolja.scbj.domain.member.validation.Phone;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.NotBlankGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.SizeGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record PaymentReadyRequest(
    @NotBlank(groups = NotBlankGroup.class)
    @Pattern(regexp = "[^0-9]*", message = "이름에 숫자는 입력할 수 없습니다.", groups = PatternGroup.class)
    @Size(min = 2, max = 20, message = "이름의 길이는 2 ~ 20자여야 합니다.", groups = SizeGroup.class)
    String customerName,
    @NotBlank(groups = NotBlankGroup.class)
    @Email(message = "유효하지 않은 이메일입니다.", groups = PatternGroup.class)
    String customerEmail,
    @Phone(groups = PatternGroup.class)
    String customerPhoneNumber,

    @JsonProperty("isAgeOver14")
    boolean isAgeOver14,
    boolean useAgree,
    boolean cancelAndRefund,
    boolean collectPersonalInfo,
    boolean thirdPartySharing
) {

    @Builder
    public PaymentReadyRequest {
    }
}
