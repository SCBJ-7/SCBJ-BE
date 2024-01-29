package com.yanolja.scbj.domain.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.yanolja.scbj.domain.alarm.dto.AlarmHasNonReadResponse;
import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.entity.Alarm;
import com.yanolja.scbj.domain.alarm.repository.AlarmRepository;
import com.yanolja.scbj.domain.alarm.util.AlarmMapper;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import com.yanolja.scbj.global.config.fcm.FCMService;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private FCMService fcmService;

    @Mock
    private MemberService memberService;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;
    @InjectMocks
    private AlarmService alarmService;
    private final Alarm alarm = Alarm.builder()
        .title("TEST용 제목")
        .content("TEST용 내용")
        .build();


    @Nested
    @DisplayName("알림 서비스 이용시, ")
    class SuccessTests {

        @Test
        @DisplayName("알람을 조회할 수 있다.")
        void getAlarms() {
            // given
            alarm.setCreatedAt(LocalDateTime.now());
            AlarmResponse expectedAlarmResponse = AlarmMapper.toAlarmResponse(alarm);

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(alarmRepository.getAllByMemberIdOrderByCreatedAtDesc(1L)).willReturn(
                List.of(alarm));
            // when
            List<AlarmResponse> resultAlarmResponses = alarmService.getAlarms();

            // then
            assertThat(resultAlarmResponses).usingRecursiveComparison()
                .isEqualTo(List.of(expectedAlarmResponse));
        }

        @Test
        @DisplayName("알람을 생성하고 푸쉬 알림을 날릴 수 있다.")
        void createAlarms() {
            //given
            Member member = Member.builder().build();
            Data data = new Data("TEST용 제목", "TEST용 메시지", LocalDateTime.now());
            PaymentHistory paymentHistory = PaymentHistory.builder().build();

            given(memberService.getMember(any(Long.class))).willReturn(member);
            given(paymentHistoryRepository.findById(1L)).willReturn(
                Optional.ofNullable(paymentHistory));
            given(alarmRepository.save(any())).willReturn(Alarm.builder().build());

            //when & then
            assertDoesNotThrow(() -> alarmService.createAlarm(1L, 1L, data));
            verify(alarmRepository, times(1)).save(any());
            verify(fcmService, times(1)).sendMessageTo(any(), any());
        }

        @Test
        @DisplayName("안 읽은 알림이 있는지 확인할 수 있다.")
        void hasNonReadAlarm() {
            //given
            AlarmHasNonReadResponse expectedalarmHasNonReadResponse = AlarmHasNonReadResponse.builder()
                .hasNonReadAlarm(true)
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(alarmRepository.existsAlarmByMemberIdAndCheckedIsFalse(1L)).willReturn(true);

            //when, then
            assertEquals(expectedalarmHasNonReadResponse, alarmService.hasNonReadAlarm());
        }
    }
}