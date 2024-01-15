package com.yanolja.scbj.domain.alarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.entity.Alarm;
import com.yanolja.scbj.domain.alarm.repository.AlarmRepository;
import com.yanolja.scbj.domain.alarm.util.AlarmMapper;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository mockAlarmRepository;

    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    private AlarmService alarmService;

    private Alarm alarm = Alarm.builder()
        .title("TEST용 제목")
        .content("TEST용 내용")
        .build();


    @Test
    @DisplayName("알람을 조회할 때")
    void getAlarms() {
        // given
        alarm.setCreatedAt(LocalDateTime.now());
        AlarmResponse expectedAlarmResponse = AlarmMapper.toAlarmResponse(alarm);

        given(securityUtil.getCurrentMemberId()).willReturn(1L);
        given(mockAlarmRepository.getAllByMemberIdOrderByCreatedAtDesc(1L)).willReturn(
            Optional.of(List.of(alarm)));
        // when
        List<AlarmResponse> resultAlarmResponses = alarmService.getAlarms();

        // then
        assertThat(resultAlarmResponses).usingRecursiveComparison()
            .isEqualTo(List.of(expectedAlarmResponse));
    }
}