package com.yanolja.scbj.domain.member.service;

import static org.junit.jupiter.api.Assertions.*;

import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.global.config.CustomUserDetailsService;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
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
    void refreshAccessToken() {

    }
}