package com.yanolja.scbj.domain.product.util;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.entity.ProductAgreement;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ProductMapper {

    public static ProductAgreement toProductAgreement(ProductPostRequest productPostRequest) {
        return ProductAgreement.builder()
            .standardTimeSellingPolicy(productPostRequest.standardTimeSellingPolicy())
            .totalAmountPolicy(productPostRequest.totalAmountPolicy())
            .sellingModificationPolicy(productPostRequest.sellingModificationPolicy())
            .productAgreement(productPostRequest.productAgreement())
            .build();
    }

    public static Product toProduct(Reservation reservation, Member member,
        ProductAgreement productAgreement, ProductPostRequest productPostRequest) {
        return Product.builder()
            .reservation(reservation)
            .member(member)
            .productAgreement(productAgreement)
            .firstPrice(productPostRequest.firstPrice())
            .secondPrice(productPostRequest.secondPrice())
            .bank(productPostRequest.bank())
            .accountNumber(productPostRequest.accountNumber())
            .secondGrantPeriod(productPostRequest.secondGrantPeriod())
            .build();
    }

    public static ProductPostResponse toProductPostResponse(Product savedProduct) {
        return ProductPostResponse.builder().productId(savedProduct.getId()).build();
    }


    public static ProductFindResponse toProductFindResponse(Hotel hotel, List<String> imageList,
        Room room, LocalDateTime checkIn, LocalDateTime checkOut, int originalPrice, int price,
        RoomThemeFindResponse roomThemeFindResponse, boolean saleStatus, boolean isSeller) {
        return ProductFindResponse.builder()
            .hotelName(hotel.getHotelName())
            .hotelImageUrlList(imageList)
            .roomName(room.getRoomName())
            .checkIn(checkIn)
            .checkOut(checkOut)
            .originalPrice(originalPrice)
            .sellingPrice(price)
            .standardPeople(room.getStandardPeople())
            .maxPeople(room.getMaxPeople())
            .bedType(room.getBedType())
            .roomTheme(roomThemeFindResponse)
            .hotelAddress(hotel.getHotelDetailAddress())
            .saleStatus(saleStatus)
            .hotelInfoUrl(hotel.getHotelInfoUrl())
            .isSeller(isSeller)
            .build();
    }
}
