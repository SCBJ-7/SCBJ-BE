package com.yanolja.scbj.domain.member.service;

import com.yanolja.scbj.domain.member.exception.EmailServerException;
import com.yanolja.scbj.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MailService {

    private final JavaMailSender emailSender;
    private String EMAIL_TITLE = "숙취방지 이메일 인증 번호";

    MailService(JavaMailSender javaMailSender) {
        this.emailSender = javaMailSender;
    }

    public String certifyEmail(final String email) {
        String authCode = createCode();
        sendEmail(email, EMAIL_TITLE, authCode);
        return authCode;
    }

    private String createCode() {
        int minCode = 100_000;
        int maxCode = 999_999;
        return String.valueOf(ThreadLocalRandom.current().nextInt(minCode, maxCode));
    }

    public void sendEmail(String to, String title, String text) {
        SimpleMailMessage email = createEmail(to, title, text);
        try {
            emailSender.send(email);
        } catch (RuntimeException e) {
            throw new EmailServerException(ErrorCode.EMAIL_SERVER_ERROR);
        }
    }

    // 발신할 이메일 데이터 세팅
    private SimpleMailMessage createEmail(String to, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(title);
        message.setText(text);

        return message;
    }
}