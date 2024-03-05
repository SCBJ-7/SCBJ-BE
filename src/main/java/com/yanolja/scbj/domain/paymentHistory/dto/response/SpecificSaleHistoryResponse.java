package com.yanolja.scbj.domain.paymentHistory.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

public record SpecificSaleHistoryResponse(
    String saleStatus,
    String checkIn,
    String checkOut,
    String hotelImage,
    int standardPeople,
    int maxPeople,
    String hotelName,
    String roomName,
    String bank,
    String accountNumber,
    firstPriceResponse firstPrice,
    secondPriceResponse secondPrice,
    LocalDateTime createdAt
) {
    @Builder
    public SpecificSaleHistoryResponse {
    }

    public record firstPriceResponse(
        int originalPrice,
        int firstSalePrice) {

        @Builder
        public firstPriceResponse {
        }
    }


    public record secondPriceResponse(
        String secondPriceStartDate,
        int secondPrice
    ) {

        @Builder
        public secondPriceResponse {
        }
    }


}
