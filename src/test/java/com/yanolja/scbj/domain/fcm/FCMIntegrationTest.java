package com.yanolja.scbj.domain.fcm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.yanolja.scbj.domain.member.service.MailService;
import com.yanolja.scbj.global.config.AbstractContainersSupport;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.config.fcm.FCMService;
import com.yanolja.scbj.global.config.fcm.FCMTokenRepository;
import com.yanolja.scbj.global.util.LocalDateTimeUtil;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class FCMIntegrationTest extends AbstractContainersSupport {

    @Autowired
    private FCMService fcmService;

    @MockBean
    private MailService mailService;

    @Autowired
    private FCMTokenRepository fcmTokenRepository;

    private final String TEST_EMAIL = "test1@gmail.com";

    private final Data test_date = new Data("TEST용 제목", "TEST용 내용",LocalDateTime.now());

    @Test
    @DisplayName("FCM을 이용해 알림을 보낼 때")
    void sendMessageTo() throws InterruptedException {
        //given
        final String FCM_TOKEN = "do0q19vcvk4:APA91bFvEOnkGTRRsjyuBTacc79wxI6POgkAj68OlC_cvDJMPMS7LAMkQXlLYOUdiWO_GLc1VaBMgjqmTpuPAINCEtYlQxVslro-hz9br1BqjbjZGy6_30p9w10_T05OhetMc5FwoKZ6";

        fcmTokenRepository.saveToken(TEST_EMAIL, FCM_TOKEN);
        Thread.sleep(1000);
        //when & then
        assertTrue(fcmTokenRepository.hasKey(TEST_EMAIL));
        assertDoesNotThrow(() -> fcmService.sendMessageTo(TEST_EMAIL,
            test_date));
        Thread.sleep(1500);
    }


    @Test
    @DisplayName("FCM을 이용해 알림을 보낼 때, 에러 핸들링으로 mail을 보낸다.")
    void sendEmailForFailureToSendAlarm() throws InterruptedException {
        //given
        final String FCM_TOKEN = "잘못된 토큰";

        fcmTokenRepository.saveToken(TEST_EMAIL, FCM_TOKEN);
        Thread.sleep(500);
        doNothing().when(mailService).sendEmail(TEST_EMAIL, test_date.getTitle(), test_date.getMessage());
        // when & then
        assertTrue(fcmTokenRepository.hasKey(TEST_EMAIL));
        assertDoesNotThrow(() -> fcmService.sendMessageTo(TEST_EMAIL, test_date));
        Thread.sleep(1500);
        verify(mailService, times(1)).sendEmail(TEST_EMAIL, test_date.getTitle(), test_date.getMessage());

    }

}
