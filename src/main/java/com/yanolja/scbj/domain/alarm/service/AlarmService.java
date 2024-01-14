package com.yanolja.scbj.domain.alarm.service;

import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.exception.AlarmNotFoundException;
import com.yanolja.scbj.domain.alarm.repository.AlarmRepository;
import com.yanolja.scbj.domain.alarm.util.AlarmMapper;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final SecurityUtil securityUtil;

    public List<AlarmResponse> getAlarms() {
        return alarmRepository.getAllByMemberIdOrderByCreatedAtDesc(
                securityUtil.getCurrentMemberId())
            .orElseThrow(() -> new AlarmNotFoundException(ErrorCode.ALARM_NOT_FOUND))
            .stream().map(AlarmMapper::toAlarmResponse).toList();
    }

}
