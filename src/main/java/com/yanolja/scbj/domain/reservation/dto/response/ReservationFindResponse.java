package com.yanolja.scbj.domain.reservation.dto.response;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationFindResponse {

    private long reservationId;
    private String hotelName;
    private String imageUrl;
    private String roomName;
    private LocalDate startDate;
    private LocalDate endDate;
    private int refundPrice;
    private int purchasePrice;
    private int remainingDays;
    private int remainingTimes;

    @Builder
    public ReservationFindResponse(long reservationId, String hotelName, String imageUrl,
        String roomName, LocalDate startDate, LocalDate endDate, int refundPrice, int purchasePrice,
        int remainingDays, int remainingTimes) {
        this.reservationId = reservationId;
        this.hotelName = hotelName;
        this.imageUrl = imageUrl;
        this.roomName = roomName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.refundPrice = refundPrice;
        this.purchasePrice = purchasePrice;
        this.remainingDays = remainingDays;
        this.remainingTimes = remainingTimes;
    }
}
