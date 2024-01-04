package com.yanolja.scbj.domain.member.service;

import com.yanolja.scbj.domain.member.controller.MemberRestController;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import com.yanolja.scbj.global.util.SecurityUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MemberService memberService;

    @Nested
    @DisplayName("성공 테스트")
    class SuccessTests {

        @Test
        void signUp() {

        }

        @Test
        void signIn() {
        }

        @Test
        void updateMemberPassword() {
        }

        @Test
        void updateMemberAccount() {
        }
    }
}