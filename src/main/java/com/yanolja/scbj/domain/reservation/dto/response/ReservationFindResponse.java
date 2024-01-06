package com.yanolja.scbj.domain.reservation.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationFindResponse {

    private String hotelName;
    private String roomName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int refundPrice;
    private int purchasePrice;
    private int remainingDays;
    private int remainingTimes;

    @Builder
    public ReservationFindResponse(String hotelName, String roomName, LocalDate startDate,
        LocalDate endDate, int refundPrice, int purchasePrice, int remainingDays,
        int remainingTimes) {
        this.hotelName = hotelName;
        this.roomName = roomName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.refundPrice = refundPrice;
        this.purchasePrice = purchasePrice;
        this.remainingDays = remainingDays;
        this.remainingTimes = remainingTimes;
    }
}
