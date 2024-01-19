package com.yanolja.scbj.domain.member.service;

import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.exception.InvalidRefreshTokenException;
import com.yanolja.scbj.domain.member.exception.NotExpiredTokenException;
import com.yanolja.scbj.global.config.CustomUserDetailsService;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import com.yanolja.scbj.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
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
        String username = null;
        try {
            username = jwtUtil.extractUsername(refreshRequest.getAccessToken().substring(7));
            throw new NotExpiredTokenException(ErrorCode.NOT_EXPIRED_TOKEN);
        } catch (ExpiredJwtException e) {
            username = e.getClaims().getSubject();
        } finally {
            if (jwtUtil.isRefreshTokenValid(username, refreshRequest.getRefreshToken())) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(username);

                return TokenResponse.builder().accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
            } else {
                throw new InvalidRefreshTokenException(ErrorCode.INVALID_REFRESH_TOKEN);
            }
        }
    }
}
