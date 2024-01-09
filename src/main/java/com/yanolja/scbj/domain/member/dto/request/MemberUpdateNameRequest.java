package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.ValidationGroups.NotBlankGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.SizeGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public record MemberUpdateNameRequest(
    @NotBlank(message = "이름의 길이는 2 ~ 20자여야 합니다.", groups = NotBlankGroup.class)
    @Pattern(regexp = "[^0-9]*", message = "이름에 숫자는 입력할 수 없습니다.", groups = PatternGroup.class)
    @Size(min = 2, max = 20, message = "이름의 길이는 2 ~ 20자여야 합니다.", groups = SizeGroup.class)
    String name

) {

    @Builder
    public MemberUpdateNameRequest(String name) {
        this.name = name;
    }
}
