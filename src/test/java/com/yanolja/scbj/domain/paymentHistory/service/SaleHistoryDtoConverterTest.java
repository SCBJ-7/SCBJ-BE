package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificSaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.service.SaleHistoryDtoConverter;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.testdata.TestData;
import com.yanolja.scbj.global.common.BaseEntity;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
 class SaleHistoryDtoConverterTest {
    @InjectMocks
    private SaleHistoryDtoConverter saleHistoryDtoConverter;



    @Test
    @DisplayName("성공시 상세 판매내역을 반환한다.")
    void will_success() {
        // given
        Member member = Member.builder()
            .id(1L)
            .name("홍길동")
            .email("hong@example.com")
            .build();

        Room room = Room.builder()
            .checkIn(LocalTime.of(15, 0))
            .checkOut(LocalTime.of(11, 0))
            .build();

        HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
            .url("image1")
            .build();

        HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
            .peakPrice(200000)
            .offPeakPrice(100000)
            .build();

        Hotel hotel = Hotel.builder()
            .hotelName("호텔 인 나인 강남")
            .room(room)
            .hotelRoomImageList(List.of(hotelRoomImage))
            .hotelRoomPrice(hotelRoomPrice)
            .build();

        Reservation reservation = Reservation.builder()
            .hotel(hotel)
            .startDate(LocalDateTime.of(2024, 1, 15, 15, 0))
            .endDate(LocalDateTime.of(2024, 1, 16, 11, 0))
            .build();

        Product product = Product.builder()
            .reservation(reservation)
            .member(member)
            .paymentHistory(PaymentHistory.builder()
                .id(1L)
                .customerName("김정훈")
                .build())
            .firstPrice(200000)
            .secondPrice(180000)
            .bank("신한은행")
            .accountNumber("123-456-7890")
            .secondGrantPeriod(6)
            .build();




        // when
        SpecificSaleHistoryResponse result =
            saleHistoryDtoConverter.toSpecificSaleHistoryResponse(product, true);

        // then
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.hotelName()).isEqualTo("호텔 인 나인 강남");
        Assertions.assertThat(result.firstPrice().originalPrice()).isEqualTo(100000);
        // 기타 필요한 필드 검증
    }

}
