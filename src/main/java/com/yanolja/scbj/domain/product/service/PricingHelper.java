package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class PricingHelper {
    public static double calculateDiscountRate(Product product, int currentPrice) {
        int originalPrice = getOriginalPrice(product.getReservation().getHotel());
        return (double) (originalPrice - currentPrice) /
            (double) originalPrice;
    }

    public static int getCurrentPrice(Product product) {
        if (product.getSecondPrice() == 0) {
            return product.getFirstPrice();
        }
        return TimeValidator.isOverSecondGrantPeriod(product,
            product.getReservation().getStartDate()) ? product.getSecondPrice() :
            product.getFirstPrice();
    }


    public static int getOriginalPrice(Hotel hotel) {
        if (TimeValidator.isPeakTime(LocalDate.now())) {
            return hotel.getHotelRoomPrice().getPeakPrice();
        }
        return hotel.getHotelRoomPrice().getOffPeakPrice();
    }
}
