package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificSaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.PaymentHistoryNotFoundException;
import com.yanolja.scbj.domain.paymentHistory.exception.SaleHistoryNotFoundException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.paymentHistory.util.PaymentHistoryMapper;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.exception.ErrorCode;
import com.yanolja.scbj.global.util.TimeValidator;
import io.grpc.netty.shaded.io.netty.util.internal.StringUtil;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final int RESERVATION_IMAGE = 0;

    private final SaleHistoryDtoConverter saleHistoryDtoConverter;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ProductRepository productRepository;

    public List<PurchasedHistoryResponse> getUsersPurchasedHistory(Long id) {
        List<PurchasedHistoryResponse> response =
            paymentHistoryRepository.findPurchasedHistoriesByMemberId(id);
        return response.isEmpty() ? Collections.emptyList() : response;
    }

    public List<SaleHistoryResponse> getUsersSaleHistory(Long id) {
        List<SaleHistoryResponse> response =
            productRepository.findSaleHistoriesByMemberId(id);
        return response.isEmpty() ? Collections.emptyList() : response;
    }

    @Transactional(readOnly = true)
    public SpecificPurchasedHistoryResponse getSpecificPurchasedHistory(Long memberId,
        Long purchaseHistoryId) {

        PaymentHistory targetPaymentHistory = paymentHistoryRepository.findByIdAndMemberId(
                purchaseHistoryId, memberId)
            .orElseThrow(() -> new PaymentHistoryNotFoundException(ErrorCode.PURCHASE_LOAD_FAIL));

        Reservation reservation = targetPaymentHistory.getProduct().getReservation();

        Hotel hotel = targetPaymentHistory.getProduct().getReservation().getHotel();

        List<HotelRoomImage> hotelRoomImageList = hotel.getHotelRoomImageList();
        String imageUrl = getImageUrl(hotelRoomImageList);

        Room room = hotel.getRoom();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy.MM.dd (E) HH:mm");
        String checkIn = reservation.getStartDate().format(dateFormatter);
        String checkOut = reservation.getEndDate().format(dateFormatter);
        String paymentHistoryDate = Optional.ofNullable(targetPaymentHistory.getCreatedAt())
            .map(date -> date.format(dateFormatter))
            .orElse(null);

        int remainingDays = (int) Duration.between(LocalDateTime.now(),
            reservation.getStartDate()).toDays();

        return PaymentHistoryMapper.toSpecificPurchasedHistoryResponse(targetPaymentHistory,
            hotel, room, checkIn, checkOut, paymentHistoryDate, getOriginalPrice(hotel),
            remainingDays == 0 ? -1 : remainingDays, imageUrl);
    }


    private int getOriginalPrice(Hotel hotel) {

        int originalPrice = hotel.getHotelRoomPrice().getOffPeakPrice();
        if (TimeValidator.isPeakTime(LocalDate.now())) {
            originalPrice = hotel.getHotelRoomPrice().getPeakPrice();
        }
        return originalPrice;
    }

    private String getImageUrl(List<HotelRoomImage> hotelRoomImageList) {

        if (hotelRoomImageList.isEmpty()) {
            return StringUtil.EMPTY_STRING;
        }
        return hotelRoomImageList.get(RESERVATION_IMAGE).getUrl();
    }

    public SpecificSaleHistoryResponse getSpecificSaleHistory(Long memberId, Long saleHistoryId, boolean isPaymentId) {

        if (isPaymentId) {

            PaymentHistory paymentHistory =
                paymentHistoryRepository.findSaleHistoryInformationById(memberId,saleHistoryId).orElseThrow(
                    () -> new SaleHistoryNotFoundException(ErrorCode.SALE_DETAIL_LOAD_FAIL));

            return saleHistoryDtoConverter.toSpecificSaleHistoryResponse(paymentHistory.getProduct(),isPaymentId);
        }

        Product responseFromProduct = productRepository.findByIdAndMemberId(saleHistoryId, memberId)
            .orElseThrow(() -> new SaleHistoryNotFoundException(ErrorCode.SALE_DETAIL_LOAD_FAIL));

        return saleHistoryDtoConverter.toSpecificSaleHistoryResponse(responseFromProduct,isPaymentId);
    }
}
