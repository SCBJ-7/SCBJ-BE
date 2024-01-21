package com.yanolja.scbj.domain.paymentHistory.dto.response;

import java.time.LocalDateTime;

public record CheckInAlarmResponse(
    long productHistoryId,

    long memberId,
    String productName,
    LocalDateTime checkIn
) {


}
