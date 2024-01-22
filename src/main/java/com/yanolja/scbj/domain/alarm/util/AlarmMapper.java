package com.yanolja.scbj.domain.alarm.util;

import com.yanolja.scbj.domain.alarm.dto.AlarmHasNonReadResponse;
import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.entity.Alarm;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AlarmMapper {


    public AlarmResponse toAlarmResponse(Alarm alarm) {
        return AlarmResponse.builder()
            .id(alarm.getId())
            .title(alarm.getTitle())
            .content(alarm.getContent())
            .date(alarm.getCreatedAt())
            .isRead(alarm.isChecked())
            .build();
    }

    public Alarm toAlarm(Member member, PaymentHistory paymentHistory, Data data) {
        return Alarm.builder()
            .member(member)
            .paymentHistory(paymentHistory)
            .title(data.getTitle())
            .content(data.getMessage())
            .build();
    }

    public AlarmHasNonReadResponse toAlarmHasNonReadResponse(boolean hasNonReadAlarm) {
        return AlarmHasNonReadResponse.builder()
            .hasNonReadAlarm(hasNonReadAlarm)
            .build();
    }

}
