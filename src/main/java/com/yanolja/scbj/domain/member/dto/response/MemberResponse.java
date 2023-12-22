package com.yanolja.scbj.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {

    private final Long id;
    private final String email;
    private final String name;
    private final String phone;

    @Builder
    private MemberResponse(Long id, String email, String name, String phone) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

}
