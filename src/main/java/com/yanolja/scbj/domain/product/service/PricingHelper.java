package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class PricingHelper {
    public static double calculateDiscountRate(Product product, int currentPrice) {
        return (double) (product.getReservation().getPurchasePrice() - currentPrice) /
            (double) product.getReservation().getPurchasePrice();
    }

    public static int getCurrentPrice(Product product) {
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
