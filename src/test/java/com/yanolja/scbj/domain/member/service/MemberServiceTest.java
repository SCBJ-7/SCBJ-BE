package com.yanolja.scbj.domain.member.service;

import com.yanolja.scbj.domain.member.controller.MemberRestController;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

class MemberServiceTest {

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