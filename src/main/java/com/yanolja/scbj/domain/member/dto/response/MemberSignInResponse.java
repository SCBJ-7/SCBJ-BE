package com.yanolja.scbj.domain.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberSignInResponse {

    private final MemberResponse memberResponse;
    private final TokenResponse tokenResponse;

    @Builder
    private MemberSignInResponse(MemberResponse memberResponse, TokenResponse tokenResponse) {
        this.memberResponse = memberResponse;
        this.tokenResponse = tokenResponse;
    }

}
