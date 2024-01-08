package com.yanolja.scbj.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.helper.TestConstants;
import com.yanolja.scbj.global.config.CustomUserDetailsService;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @InjectMocks
    private MemberAuthService memberAuthService;


    @Test
    @DisplayName("리프레쉬 토큰 재발급할 때")
    void refreshAccessToken() {
        RefreshRequest refreshRequest = RefreshRequest.builder()
            .refreshToken(TestConstants.REFRESH_PREFIX.getValue())
            .accessToken(TestConstants.GRANT_TYPE.getValue())
            .build();
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken(TestConstants.GRANT_TYPE.getValue())
            .refreshToken(TestConstants.REFRESH_PREFIX.getValue())
            .build();

        given(jwtUtil.isRefreshTokenValid(any(), any())).willReturn(true);
        given(jwtUtil.generateToken(any())).willReturn(TestConstants.GRANT_TYPE.getValue());
        given(jwtUtil.generateRefreshToken(any())).willReturn(
            TestConstants.REFRESH_PREFIX.getValue());

        //when & then
        assertThat(tokenResponse).usingRecursiveComparison()
            .isEqualTo(memberAuthService.refreshAccessToken(refreshRequest));
    }
}