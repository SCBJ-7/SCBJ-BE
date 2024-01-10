package com.yanolja.scbj.domain.product.dto.request;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductSearchRequest {

    private String location;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer quantityPeople;
    private String sorted;
    private Boolean parking;
    private Boolean brunch;
    private Boolean pool;
    private Boolean oceanView;

    @Builder
    public ProductSearchRequest(String location, LocalDate checkIn, LocalDate checkOut, Integer quantityPeople, String sorted,
                                Boolean parking, Boolean brunch, Boolean pool, Boolean oceanView) {
        this.location = location;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.quantityPeople = quantityPeople;
        this.sorted = sorted;
        this.parking = parking;
        this.brunch = brunch;
        this.pool = pool;
        this.oceanView = oceanView;
    }
}
