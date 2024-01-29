package com.yanolja.scbj.domain.alarm.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record AlarmResponse(
    Long id,
    String title,
    String content,

    LocalDateTime date,

    boolean isRead
) {

}
