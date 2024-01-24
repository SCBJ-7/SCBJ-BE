package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.product.dto.response.CityResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CityDtoMapper {
    public static CityResponse toCityResponse(Product product, String hotelUrl , Reservation reservation, int currentPrice, double discountRate , int originalPrice) {
        return CityResponse.builder()
            .id(product.getId())
            .city(reservation.getHotel().getHotelMainAddress())
            .imageUrl(hotelUrl)
            .hotelName(reservation.getHotel().getHotelName())
            .roomType(reservation.getHotel().getRoom().getBedType())
            .originalPrice(originalPrice)
            .salePrice(currentPrice)
            .salePercentage(discountRate)
            .checkInDate(reservation.getStartDate())
            .checkOutDate(reservation.getEndDate())
            .build();
    }
}
