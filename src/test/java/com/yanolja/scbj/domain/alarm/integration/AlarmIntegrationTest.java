package com.yanolja.scbj.domain.alarm.integration;

import static org.junit.Assert.assertNotEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.alarm.entity.Alarm;
import com.yanolja.scbj.domain.alarm.repository.AlarmRepository;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.global.config.AbstractContainersSupport;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.factory.TestEntityFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class AlarmIntegrationTest extends AbstractContainersSupport {

    @Autowired
    private AlarmService alarmService;

    @Autowired
    private EntityManager entityManager;

    private TestEntityFactory testEntityFactory;

    @Autowired
    private AlarmRepository alarmRepository;


    @BeforeEach
    void init() {
        testEntityFactory = new TestEntityFactory(entityManager);
        testEntityFactory.createTestEnvironment(LocalTime.now().plusSeconds(30),
            LocalDateTime.now().plusDays(1));
    }

    @Test
    @DisplayName("결제 내역에 대해 체크인 24시간 전 알림을 받을 수 있다.")
    void alarmBeforeCheckIn() {
        alarmService.AlarmBeforeCheckIn();
    }

    @Test
    @DisplayName("알람을 생성할 수 있다.")
    void createAlarm() {
        alarmService.createAlarm(1L, 1L, new Data("테스트용", "테스트용", LocalDateTime.now()));
        Alarm alarm = alarmRepository.findById(1L).orElseGet(null);
        assertNotEquals(alarm, null);
    }

}
