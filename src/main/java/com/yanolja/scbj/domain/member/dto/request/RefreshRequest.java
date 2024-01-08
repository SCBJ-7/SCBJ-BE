package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.AccessToken;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshRequest {

    @AccessToken
    private String accessToken;
    @NotBlank(message = "유효하지 않은 리프레쉬 토큰입니다.")
    private String refreshToken;

    @Builder
    private RefreshRequest(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}