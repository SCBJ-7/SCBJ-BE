package com.yanolja.scbj.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.util.MemberMapper;
import com.yanolja.scbj.global.config.CustomUserDetailsService;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class MemberAuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @InjectMocks
    private MemberAuthService memberAuthService;


    @Test
    void refreshAccessToken() {
        RefreshRequest refreshRequest = RefreshRequest.builder().refreshToken("").accessToken("")
            .build();
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken("")
                .refreshToken("")
                    .build();

        given(jwtUtil.isRefreshTokenValid(any(), any())).willReturn(true);
        given(jwtUtil.generateToken(any())).willReturn("");
        given(jwtUtil.generateRefreshToken(any())).willReturn("");

        //when & then
        assertThat(tokenResponse).usingRecursiveComparison().isEqualTo(memberAuthService.refreshAccessToken(refreshRequest));
    }
}