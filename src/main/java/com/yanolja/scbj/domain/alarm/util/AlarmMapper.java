package com.yanolja.scbj.domain.alarm.util;

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

    private static String LOCAL_DATE_TIME_PATTERN = "yyyy.MM.dd. hh:mma";

    public AlarmResponse toAlarmResponse(Alarm alarm) {
        return AlarmResponse.builder()
            .id(alarm.getId())
            .title(alarm.getTitle())
            .content(alarm.getContent())
            .date(toStringDate(alarm.getCreatedAt()))
            .build();
    }

    public String toStringDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern(LOCAL_DATE_TIME_PATTERN));
    }

    public Alarm toAlarm(Member member, PaymentHistory paymentHistory, Data data) {
        return Alarm.builder()
            .member(member)
            .paymentHistory(paymentHistory)
            .title(data.getTitle())
            .content(data.getMessage())
            .build();
    }

}
