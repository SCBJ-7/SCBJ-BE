package com.yanolja.scbj.domain.alarm.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.global.config.AbstractContainersSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlarmIntegrationTest extends AbstractContainersSupport {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @BeforeEach
    void init() {
        //
    }

    private
    @Test
    @DisplayName("결제 내역에 대해 체크인 24시간 전 알림을 받을 수 있다.")
    void alarmBeforeCheckIn() {

    }

}
