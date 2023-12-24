package com.yanolja.scbj.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshRequest {

    @NotBlank(message = "Access Token 을 입력하세요.")
    private String accessToken;
    @NotBlank(message = "Refresh Token 을 입력하세요.")
    private String refreshToken;

    @Builder
    private RefreshRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}