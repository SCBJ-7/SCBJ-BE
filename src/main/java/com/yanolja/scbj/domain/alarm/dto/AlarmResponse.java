package com.yanolja.scbj.domain.alarm.dto;

import lombok.Builder;

@Builder
public record AlarmResponse(
    Long id,
    String title,
    String content,
    String date
) {

}
