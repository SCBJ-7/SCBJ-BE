package com.yanolja.scbj.domain.member.dto.request;

import com.yanolja.scbj.domain.member.validation.AccessToken;
import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshRequest {

    @AccessToken(groups = PatternGroup.class)
    private String accessToken;
    @NotBlank(groups = PatternGroup.class)
    private String refreshToken;

    @Builder
    private RefreshRequest(String accessToken, String refreshToken, String fcmToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}