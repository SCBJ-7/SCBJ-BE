package com.yanolja.scbj.domain.member.controller;

import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.global.config.AbstractContainersSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MemberRestControllerTest extends AbstractContainersSupport {

    @Mock
    private MemberService memberService;

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