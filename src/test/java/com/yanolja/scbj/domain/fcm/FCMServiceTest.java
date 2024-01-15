package com.yanolja.scbj.domain.fcm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yanolja.scbj.global.config.AbstractContainersSupport;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.config.fcm.FCMService;
import com.yanolja.scbj.global.config.fcm.FCMTokenRepository;
import com.yanolja.scbj.global.util.LocalDateTimeUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FCMServiceTest extends AbstractContainersSupport {

    @Autowired
    private FCMService fcmService;

    @Autowired
    private FCMTokenRepository fcmTokenRepository;

    @Test
    @DisplayName("FCM을 이용해 알림을 보낼 때")
    void sendMessageTo() {
        //given
        final String TEST_EMAIL = "test1@gmail.com";
        final String FCM_TOKEN = "do0q19vcvk4:APA91bFvEOnkGTRRsjyuBTacc79wxI6POgkAj68OlC_cvDJMPMS7LAMkQXlLYOUdiWO_GLc1VaBMgjqmTpuPAINCEtYlQxVslro-hz9br1BqjbjZGy6_30p9w10_T05OhetMc5FwoKZ6";
        fcmService.saveToken(TEST_EMAIL, FCM_TOKEN);

        //when & then
        assertTrue(fcmTokenRepository.hasKey(TEST_EMAIL));
        assertDoesNotThrow(() -> fcmService.sendMessageTo(TEST_EMAIL,
            new Data("TEST용 제목", "TEST용 내용",
                LocalDateTimeUtil.convertToString(LocalDateTime.now()))));
    }
}
