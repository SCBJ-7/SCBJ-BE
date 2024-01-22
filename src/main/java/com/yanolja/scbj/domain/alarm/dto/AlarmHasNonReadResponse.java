package com.yanolja.scbj.domain.alarm.dto;

import lombok.Builder;

@Builder
public record AlarmHasNonReadResponse (
    boolean hasNonReadAlarm
){


}
