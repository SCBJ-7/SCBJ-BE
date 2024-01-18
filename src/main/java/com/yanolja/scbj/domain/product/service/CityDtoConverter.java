package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.product.dto.response.CityResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CityDtoConverter {
    private static final long PRODUCT_QUANTITY = 2;


    public List<CityResponse> toCityResponse(List<Product> productByCity) {
        return productByCity.stream()
            .map(product -> {
                Reservation reservation = product.getReservation();
                int currentPrice = PricingHelper.getCurrentPrice(product);
                double discountRate = PricingHelper.calculateDiscountRate(product, currentPrice);
                String hotelUrl = getHotelUrl(product.getReservation().getHotel());

                return CityResponse.builder()
                    .id(product.getId())
                    .city(reservation.getHotel().getHotelMainAddress())
                    .imageUrl(hotelUrl)
                    .hotelName(reservation.getHotel().getHotelName())
                    .roomType(reservation.getHotel().getRoom().getBedType())
                    .originalPrice(reservation.getPurchasePrice())
                    .salePrice(currentPrice)
                    .salePercentage(discountRate)
                    .checkInDate(reservation.getStartDate())
                    .checkOutDate(reservation.getEndDate())
                    .build();

            }).sorted(Comparator.comparingDouble(CityResponse::salePercentage))
            .limit(PRODUCT_QUANTITY)
            .toList();


    }

    private String getHotelUrl(Hotel hotel) {
        return hotel.getHotelRoomImageList().isEmpty() ? null :
            hotel.getHotelRoomImageList().get(0).getUrl();
    }
}
