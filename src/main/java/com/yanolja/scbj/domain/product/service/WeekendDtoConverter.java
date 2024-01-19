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
            .sorted(ascendCheckin()
                .thenComparing(descendSalePercentage())
                .thenComparing(descendRoomThemeCount()))
            .collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), responses.size());

        return new PageImpl<>(responses.subList(start, end), pageable, responses.size());
    }

    private Comparator<WeekendProductResponse> ascendCheckin() {
        return Comparator
            .comparing(WeekendProductResponse::checkInDate);
    }
    private Comparator<WeekendProductResponse> descendSalePercentage() {
        return Comparator.comparing(WeekendProductResponse::salePercentage, Comparator.reverseOrder());
    }

    private Comparator<WeekendProductResponse> descendRoomThemeCount() {
        return Comparator.comparing(WeekendProductResponse::roomThemeCount, Comparator.reverseOrder());
    }

    private int getThemeCount(RoomTheme roomTheme) {
        return (roomTheme.isBreakfast() ? 1 : 0) +
            (roomTheme.isPool() ? 1 : 0) +
            (roomTheme.isOceanView() ? 1 : 0);
    }

    private String getHotelUrl(Hotel hotel) {
        return hotel.getHotelRoomImageList().isEmpty() ? null :
            hotel.getHotelRoomImageList().get(0).getUrl();
    }
}
