package com.yanolja.scbj.domain.payment.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.payment.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final int FIRST_IMAGE = 0;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PaymentPageFindResponse getPaymentPage(Long productId){
        Product targetProduct = productRepository.findProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));

        Reservation targetReservation = targetProduct.getReservation();
        Hotel targetHotel = targetReservation.getHotel();
        Room targetRoom = targetHotel.getRoom();
        HotelRoomPrice targetHotelRoomPrice = targetHotel.getHotelRoomPrice();
        List<HotelRoomImage> targetHotelRoomImageList = targetHotel.getHotelRoomImageList();

        int originalPrice = targetHotelRoomPrice.getOffPeakPrice();

        if(TimeValidator.isPeakTime(LocalDate.now())){
            originalPrice = targetHotelRoomPrice.getPeakPrice();
        }

        LocalDateTime checkInDateTime = LocalDateTime.of(targetReservation.getStartDate(),
            targetRoom.getCheckIn());

        LocalDateTime checkOutDateTime = LocalDateTime.of(targetReservation.getEndDate(),
            targetRoom.getCheckOut());

        int price = targetProduct.getFirstPrice();
        if (TimeValidator.isOverSecondGrantPeriod(targetProduct, checkInDateTime)) {
            price = targetProduct.getSecondPrice();
        }

        return PaymentPageFindResponse.builder()
            .hotelImage(targetHotelRoomImageList.get(FIRST_IMAGE).getUrl())
            .hotelName(targetHotel.getHotelName())
            .roomName(targetRoom.getRoomName())
            .standardPeople(targetRoom.getStandardPeople())
            .maxPeople(targetRoom.getMaxPeople())
            .checkInDateTime(checkInDateTime)
            .checkOutDateTime(checkOutDateTime)
            .originalPrice(originalPrice)
            .salePrice(price)
            .build();
    }
}
