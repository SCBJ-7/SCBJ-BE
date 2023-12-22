package com.yanolja.scbj.domain.member.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class MemberAuthService {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    MemberAuthService(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    public TokenResponse refreshAccessToken(final RefreshRequest refreshRequest) {
        String username = jwtUtil.extractUsername(refreshRequest.getAccessToken());
        if (jwtUtil.isRefreshTokenValid(username, refreshRequest.getRefreshToken())) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(username);

            return TokenResponse.builder().accessToken(newAccessToken).refreshToken(newRefreshToken)
                .build();

        } else {
            throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

}
