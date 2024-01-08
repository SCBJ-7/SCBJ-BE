package com.yanolja.scbj.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {
    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private MailService mailService;

    @Test
    @DisplayName("이메일 인증을 위한 인증번호 발급 시")
    void certifyEmail() {
        //given
        String email = "wocjf0513@ajou.ac.kr";
        //when
        String resultAuthCode= mailService.certifyEmail(email);
        //then
        assertThat(resultAuthCode).isNotBlank().hasSize(6);
    }
}
