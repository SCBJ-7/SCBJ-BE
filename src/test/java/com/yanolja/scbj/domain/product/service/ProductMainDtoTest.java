package com.yanolja.scbj.domain.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.response.WeekendProductResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ProductMainDtoTest {

    @InjectMocks
    private CityMapper cityMapper;

    @InjectMocks
    private WeekendMapper weekendMapper;

    @Mock
    private PricingHelper pricingHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private List<Product> createMockProducts() {
        List<Product> products = new ArrayList<>();

        RoomTheme roomTheme = RoomTheme.builder()
            .id(1L)
            .build();
        Room room = Room.builder()
            .checkIn(LocalTime.now().plusHours(24))
            .checkOut(LocalTime.now().plusHours(48))
            .roomTheme(roomTheme)

            .build();

        HotelRoomPrice hotelRoomPrice = HotelRoomPrice.builder()
            .id(1L)
            .offPeakPrice(100000)
            .peakPrice(200000)
            .build();

        Hotel hotel = Hotel.builder()
            .id(1L)
            .room(room)
            .hotelRoomImageList(List.of(HotelRoomImage.builder().build()))
            .hotelRoomPrice(hotelRoomPrice)
            .build();

        Reservation reservation = Reservation.builder()
            .id(1L)
            .hotel(hotel)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .build();

        Product product = Product.builder()
            .id(1L)
            .firstPrice(200000)
            .secondPrice(100000)
            .bank("국민")
            .accountNumber("12512-2131-12512")
            .secondGrantPeriod(24)
            .reservation(reservation)
            .build();

        products.add(product);
        products.add(product);

        return products;
    }

    @Nested
    @DisplayName("주말 상품은")
    class Context_weekendDtoConverterTest {

        @Test
        @DisplayName("값을 넣으면 그만큼의 size가 뜬다")
        void testToWeekendProductResponse() {
            // given
            List<Product> products = createMockProducts();
            Product product = products.get(0);
            RoomTheme roomTheme = product.getReservation().getHotel().getRoom().getRoomTheme();

            // when
            WeekendProductResponse response =
                WeekendMapper.toWeekendProductResponse(product, product.getReservation(),
                    "image", 200000, 0.6, 2, roomTheme);

            // then
            assertEquals(response.imageUrl(), "image");
        }
    }


}
