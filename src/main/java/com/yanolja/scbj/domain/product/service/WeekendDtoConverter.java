package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.response.WeekendProductResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WeekendDtoConverter {

    public Page<WeekendProductResponse> toWeekendProductResponse(List<Product> weekendProducts , Pageable pageable) {
        List<WeekendProductResponse> responses = weekendProducts.stream()
            .map(product -> {
                Reservation reservation = product.getReservation();
                RoomTheme roomTheme = reservation.getHotel().getRoom().getRoomTheme();
                String hotelUrl = getHotelUrl(product.getReservation().getHotel());
                int currentPrice = PricingHelper.getCurrentPrice(product);
                double discountRate = PricingHelper.calculateDiscountRate(product, currentPrice);

                return WeekendProductResponse.builder()
                    .id(product.getId())
                    .hotelName(reservation.getHotel().getHotelName())
                    .roomType(reservation.getHotel().getRoom().getBedType())
                    .imageUrl(hotelUrl)
                    .originalPrice(reservation.getPurchasePrice())
                    .salePrice(currentPrice)
                    .salePercentage(discountRate)
                    .checkInDate(reservation.getStartDate())
                    .checkOutDate(reservation.getEndDate())
                    .isBrunchIncluded(roomTheme.isBreakfast())
                    .isPoolIncluded(roomTheme.isPool())
                    .isOceanViewIncluded(roomTheme.isOceanView())
                    .roomThemeCount(getThemeCount(roomTheme))
                    .build();

            })
            .sorted(Comparator
                .comparing(WeekendProductResponse::checkInDate)
                .thenComparing(WeekendProductResponse::salePercentage, Comparator.reverseOrder())
                .thenComparing(WeekendProductResponse::roomThemeCount, Comparator.reverseOrder()))
            .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());

        return new PageImpl<>(responses.subList(start, end), pageable, responses.size());
    }

    private int getThemeCount(RoomTheme roomTheme) {
        int count = 0;
        if (roomTheme.isBreakfast()) count++;
        if (roomTheme.isPool()) count++;
        if (roomTheme.isOceanView()) count++;
        return count;
    }

    private String getHotelUrl(Hotel hotel) {
        return hotel.getHotelRoomImageList().isEmpty() ? null :
            hotel.getHotelRoomImageList().get(0).getUrl();
    }
}
