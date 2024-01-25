package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.response.WeekendProductResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WeekendMapper {


    public static WeekendProductResponse toWeekendProductResponse(Product product,
                                                           Reservation reservation,
                                                           String hotelUrl, int currentPrice,
                                                           double discountRate,
                                                           int roomThemeCount,
                                                           RoomTheme roomTheme) {

        return WeekendProductResponse.builder()
            .id(product.getId())
            .hotelName(reservation.getHotel().getHotelName())
            .roomType(reservation.getHotel().getRoom().getRoomName())
            .imageUrl(hotelUrl)
            .originalPrice(PricingHelper.getOriginalPrice(reservation.getHotel()))
            .salePrice(currentPrice)
            .salePercentage(discountRate)
            .checkInDate(reservation.getStartDate())
            .checkOutDate(reservation.getEndDate())
            .isBrunchIncluded(roomTheme.isBreakfast())
            .isPoolIncluded(roomTheme.isPool())
            .isOceanViewIncluded(roomTheme.isOceanView())
            .roomThemeCount(roomThemeCount)
            .build();

    }
}
