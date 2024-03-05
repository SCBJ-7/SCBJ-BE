package com.yanolja.scbj.domain.paymentHistory.util;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.paymentHistory.dto.redis.PaymentRedisDto;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentAgreement;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentHistoryMapper {

    private static final String PAYMENT_TYPE = "카카오페이";

    public static SpecificPurchasedHistoryResponse toSpecificPurchasedHistoryResponse(
        PaymentHistory paymentHistory, Hotel hotel, Room room, String checkIn, String checkOut,
        String paymentHistoryDate, int originalPrice, int remainingDays, String imageUrl) {
        return SpecificPurchasedHistoryResponse.builder()
            .hotelName(hotel.getHotelName())
            .roomName(room.getRoomName())
            .standardPeople(room.getStandardPeople())
            .maxPeople(room.getMaxPeople())
            .checkIn(checkIn)
            .checkOut(checkOut)
            .customerName(paymentHistory.getCustomerName())
            .customerPhoneNumber(paymentHistory.getCustomerPhoneNumber())
            .paymentHistoryId(paymentHistory.getId())
            .originalPrice(originalPrice)
            .price(paymentHistory.getPrice())
            .remainingDays(remainingDays)
            .paymentHistoryDate(paymentHistoryDate)
            .hotelImage(imageUrl)
            .build();
    }

    public static PaymentHistory toPaymentHistory(Member buyer, PaymentAgreement paymentAgreement,
        PaymentRedisDto paymentInfo, Product product) {

        return PaymentHistory.builder()
            .member(buyer)
            .productName(paymentInfo.productName())
            .product(product)
            .customerName(paymentInfo.customerName())
            .customerEmail(paymentInfo.customerEmail())
            .customerPhoneNumber(paymentInfo.customerPhoneNumber())
            .paymentAgreement(paymentAgreement)
            .price(paymentInfo.price())
            .paymentType(PAYMENT_TYPE)
            .build();
    }

    public static PaymentPageFindResponse toPaymentPageFindResponse(String url, Hotel hotel,
        Product product, int originalPrice, int salePrice) {
        Room room = hotel.getRoom();

        return PaymentPageFindResponse.builder()
            .hotelImage(url)
            .hotelName(hotel.getHotelName())
            .roomName(room.getRoomName())
            .standardPeople(room.getStandardPeople())
            .maxPeople(room.getMaxPeople())
            .checkInDateTime(product.getReservation().getStartDate())
            .checkOutDateTime(product.getReservation().getEndDate())
            .originalPrice(originalPrice)
            .salePrice(salePrice)
            .build();
    }


}
