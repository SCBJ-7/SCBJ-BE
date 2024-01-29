package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.paymentHistory.util.PaymentHistoryMapper;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.exception.ProductNotFoundException;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.TimeValidator;
import io.netty.util.internal.StringUtil;
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
    public PaymentPageFindResponse getPaymentPage(Long productId) {
        Product targetProduct = productRepository.findProductById(productId)
            .orElseThrow(() -> new ProductNotFoundException(
                ErrorCode.PRODUCT_NOT_FOUND));

        Hotel targetHotel = targetProduct.getReservation().getHotel();

        int originalPrice = getOriginalPrice(targetHotel);
        int salePrice = getSalePrice(targetProduct);

        return PaymentHistoryMapper.toPaymentPageFindResponse(
            getFirstImageUrl(targetHotel), targetHotel, targetProduct, originalPrice,
            salePrice);
    }

    private int getOriginalPrice(Hotel hotel){
        HotelRoomPrice targetHotelRoomPrice = hotel.getHotelRoomPrice();
        int originalPrice = targetHotelRoomPrice.getOffPeakPrice();
        if (TimeValidator.isPeakTime(LocalDate.now())) {
            originalPrice = targetHotelRoomPrice.getPeakPrice();
        }

        return originalPrice;
    }

    private int getSalePrice(Product product){
        LocalDateTime checkInDateTime = product.getReservation().getStartDate();

        int price = product.getFirstPrice();
        if (TimeValidator.isOverSecondGrantPeriod(product, checkInDateTime)) {
            price = product.getSecondPrice();
        }

        return price;
    }

    private String getFirstImageUrl(Hotel hotel){
        List<HotelRoomImage> hotelRoomImageList = hotel.getHotelRoomImageList();
        String url = StringUtil.EMPTY_STRING;
        if (!hotelRoomImageList.isEmpty()) {
            url = hotelRoomImageList.get(FIRST_IMAGE).getUrl();
        }
        return url;
    }
}