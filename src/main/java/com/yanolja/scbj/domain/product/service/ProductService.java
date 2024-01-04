package com.yanolja.scbj.domain.product.service;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.ProductFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.SeasonValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public ProductFindResponse findProduct(Long productId) {
        Product foundProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        Reservation foundReservation = foundProduct.getReservation();
        Hotel foundHotel = foundReservation.getHotel();
        Room foundRoom = foundHotel.getRoom();

        LocalDateTime checkInDateTime = LocalDateTime.of(foundReservation.getStartDate(),
            foundRoom.getCheckIn());
        LocalDateTime checkOutDateTime = LocalDateTime.of(foundReservation.getEndDate(),
            foundRoom.getCheckOut());

        int price = foundHotel.getHotelRoomPrice().getOffPeakPrice();

        if (SeasonValidator.isPeakTime(LocalDate.now())) {
            price = foundHotel.getHotelRoomPrice().getPeakPrice();
        }

        RoomTheme foundRoomTheme = foundRoom.getRoomTheme();

        RoomThemeFindResponse roomThemeResponse = RoomThemeFindResponse.builder()
            .parkingZone(foundRoomTheme.hasParkingZone())
            .breakfast(foundRoomTheme.hasBreakfast())
            .pool(foundRoomTheme.hasPool())
            .oceanView(foundRoomTheme.hasOceanView())
            .build();

        return ProductFindResponse.builder()
            .hotelName(foundHotel.getHotelName())
            .roomName(foundRoom.getRoomName())
            .checkIn(checkInDateTime)
            .checkOut(checkOutDateTime)
            .originalPrice(price)
            .sellingPrice(foundReservation.getPurchasePrice())
            .standardPeople(foundRoom.getStandardPeople())
            .maxPeople(foundRoom.getMaxPeople())
            .bedType(foundRoom.getBedType())
            .roomTheme(roomThemeResponse)
            .hotelAddress(foundHotel.getHotelDetailAddress())
            .saleStatus(getSaleStatus(foundProduct))
            .hotelInfoUrl(foundHotel.getHotelInfoUrl())
            .build();
    }

    private boolean getSaleStatus(Product product) {
        return product.getPaymentHistory() != null;
    }

}
