package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.util.SecurityUtil;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductDtoConverter {
    private final int OUT_OF_STOCK = 0;
    private final SecurityUtil securityUtil;

    public ProductFindResponse toFindResponse(Product product) {

        Reservation foundReservation = product.getReservation();
        Hotel foundHotel = foundReservation.getHotel();
        Room foundRoom = foundHotel.getRoom();

        LocalDateTime checkInDateTime = foundReservation.getStartDate();
        LocalDateTime checkOutDateTime = foundReservation.getEndDate();

        int originalPrice = foundHotel.getHotelRoomPrice().getOffPeakPrice();

        if (TimeValidator.isPeakTime(LocalDate.now())) {
            originalPrice = foundHotel.getHotelRoomPrice().getPeakPrice();
        }

        RoomTheme foundRoomTheme = foundRoom.getRoomTheme();

        RoomThemeFindResponse roomThemeResponse = RoomThemeFindResponse.builder()
            .parkingZone(foundRoomTheme.hasParkingZone())
            .breakfast(foundRoomTheme.hasBreakfast())
            .pool(foundRoomTheme.hasPool())
            .oceanView(foundRoomTheme.hasOceanView())
            .build();

        int price = product.getFirstPrice();

        if (TimeValidator.isOverSecondGrantPeriod(product, checkInDateTime)) {
            price = product.getSecondPrice();
        }

        List<HotelRoomImage> hotelRoomImageList = foundHotel.getHotelRoomImageList();
        List<String> imageUrlList = new ArrayList<>();
        for (HotelRoomImage hotelRoomImage : hotelRoomImageList) {
            imageUrlList.add(hotelRoomImage.getUrl());
        }

        return ProductFindResponse.builder()
            .hotelName(foundHotel.getHotelName())
            .hotelImageUrlList(imageUrlList)
            .roomName(foundRoom.getRoomName())
            .checkIn(checkInDateTime)
            .checkOut(checkOutDateTime)
            .originalPrice(originalPrice)
            .sellingPrice(price)
            .standardPeople(foundRoom.getStandardPeople())
            .maxPeople(foundRoom.getMaxPeople())
            .bedType(foundRoom.getBedType())
            .roomTheme(roomThemeResponse)
            .hotelAddress(foundHotel.getHotelDetailAddress())
            .saleStatus(getSaleStatus(product, checkInDateTime))
            .hotelInfoUrl(foundHotel.getHotelInfoUrl())
            .isSeller(checkSeller(product))
            .build();
    }

    private boolean getSaleStatus(Product product, LocalDateTime checkIn) {
        if (product.getStock() == OUT_OF_STOCK){
            return false;
        }
        return LocalDateTime.now().isAfter(checkIn);
    }

    private boolean checkSeller(Product product) {
        if (securityUtil.isUserNotAuthenticated()) {
            return false;
        }
        return product.getMember().getId() == securityUtil.getCurrentMemberId();
    }

}
