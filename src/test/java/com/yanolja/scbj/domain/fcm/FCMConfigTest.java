package com.yanolja.scbj.domain.fcm;

import com.yanolja.scbj.global.config.AbstractContainersSupport;
import com.yanolja.scbj.global.config.fcm.FCMRequest;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.config.fcm.FCMService;
import com.yanolja.scbj.global.util.LocalDateTimeUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class FCMConfigTest extends AbstractContainersSupport {

    @Autowired
    private FCMService fcmService;

    @Test
    @DisplayName("FCM 설정이 잘 됐을 때")
    void isConfiguredFCM() {
        fcmService.saveToken("test@gmail.com", "TEST용 FCM 토큰");
        fcmService.sendMessageTo("test@gmail.com",
            new Data("TEST용 제목", "TEST용 내용", LocalDateTimeUtil.convertToString(
                LocalDateTime.now())));


    }
}
