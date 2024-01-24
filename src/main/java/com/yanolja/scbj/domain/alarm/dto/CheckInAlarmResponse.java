package com.yanolja.scbj.domain.alarm.dto;


public record CheckInAlarmResponse(
    long productHistoryId,

    long memberId,
    String productName
) {


}
