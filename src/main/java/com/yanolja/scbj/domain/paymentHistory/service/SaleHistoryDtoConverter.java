package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificSaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.util.TimeValidator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class SaleHistoryDtoConverter {
    public SpecificSaleHistoryResponse toSpecificSaleHistoryResponse(
        PaymentHistory paymentHistory
    ) {

        Product product = paymentHistory.getProduct();
        Reservation reservation = product.getReservation();
        Hotel hotel = product.getReservation().getHotel();
        Room room = hotel.getRoom();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yy.MM.dd (E) ");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String status = calculateStatus(paymentHistory, reservation);
        String hotelUrl = getHotelUrl(hotel);
        String hotelName = hotel.getHotelName();
        String roomName = room.getRoomName();
        String formattedCheckIn = formatCheckIn(reservation, dateFormatter, hotel, timeFormatter);
        String formattedCheckOut = formatCheckOut(reservation, dateFormatter, hotel, timeFormatter);

        int standardPeople = room.getStandardPeople();
        int maxPeople = room.getMaxPeople();
        int originalPrice = checkPeakPrice(hotel);
        int firstPrice = product.getFirstPrice();
        int secondPrice = product.getSecondPrice();

        String bank = product.getBank();
        String accountNumber = product.getAccountNumber();

        SpecificSaleHistoryResponse.firstPriceResponse firstPriceObject =
            SpecificSaleHistoryResponse.firstPriceResponse.builder()
                .originalPrice(originalPrice)
                .firstSalePrice(firstPrice)
                .build();

        SpecificSaleHistoryResponse.secondPriceResponse secondPriceObject =
            SpecificSaleHistoryResponse.secondPriceResponse.builder()
                .secondPrice(secondPrice)
                .secondPriceStartDate(
                    calculateSecondPriceStartDate(reservation, product, dateTimeFormatter))
                .build();

        return SpecificSaleHistoryResponse.builder()
            .saleStatus(status)
            .hotelImage(hotelUrl)
            .hotelName(hotelName)
            .roomName(roomName)
            .checkIn(formattedCheckIn)
            .checkOut(formattedCheckOut)
            .standardPeople(standardPeople)
            .maxPeople(maxPeople)
            .bank(bank)
            .accountNumber(accountNumber)
            .firstPrice(firstPriceObject)
            .secondPrice(secondPriceObject)
            .build();
    }

    private String calculateStatus(PaymentHistory paymentHistory, Reservation reservation) {
        return Optional.ofNullable(paymentHistory)
            .map(ph -> paymentHistory.isSettlement() ? "정산완료" : "거래완료")
            .orElseGet(() ->
                reservation.getEndDate().isBefore(LocalDateTime.now()) ? "판매만료" : "판매중"
            );
    }

    private String getHotelUrl(Hotel hotel) {
        return hotel.getHotelRoomImageList().isEmpty() ? null :
            hotel.getHotelRoomImageList().get(0).getUrl();
    }

    private String formatCheckIn(Reservation reservation, DateTimeFormatter dateFormatter,
                                 Hotel hotel, DateTimeFormatter timeFormatter) {
        return reservation.getStartDate().format(dateFormatter) + hotel.getRoom().getCheckIn()
            .format(timeFormatter);


    }

    private String formatCheckOut(Reservation reservation, DateTimeFormatter dateFormatter,
                                  Hotel hotel, DateTimeFormatter timeFormatter) {
        return reservation.getEndDate().format(dateFormatter) + hotel.getRoom().getCheckOut()
            .format(timeFormatter);

    }

    private int checkPeakPrice(Hotel hotel) {
        if (TimeValidator.isPeakTime(LocalDate.now())) {
            return hotel.getHotelRoomPrice().getPeakPrice();
        }
        return hotel.getHotelRoomPrice().getOffPeakPrice();
    }

    private String calculateSecondPriceStartDate(Reservation reservation, Product product,
                                                 DateTimeFormatter dateTimeFormatter) {
        return reservation.getStartDate().minusHours(product.getSecondGrantPeriod())
            .format(dateTimeFormatter);
    }


}
