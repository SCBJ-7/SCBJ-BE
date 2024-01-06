package com.yanolja.scbj.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String phone;
    private final boolean linkedToYanolja;

    @Builder
    private MemberResponse(Long id, String email, String name, String phone, boolean linkedToYanolja) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }
}
