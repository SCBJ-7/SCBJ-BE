package com.yanolja.scbj.domain.product.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomPrice;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.response.ProductMainResponse;
import com.yanolja.scbj.domain.product.dto.response.WeekendProductResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

public class ProductMainDtoTest {

    @InjectMocks
    private CityMapper cityMapper;

    @InjectMocks
    ProductService productService;
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
            .roomAllRating("4.5")

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
            .hotelLevel("5성급")
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
            String roomRate = product.getReservation().getHotel().getRoom().getRoomAllRating();
            String hotelRate = product.getReservation().getHotel().getHotelLevel();

            // when
            WeekendProductResponse response =
                WeekendMapper.toWeekendProductResponse(product, product.getReservation(),
                    "image", 200000, 0.6, 2, roomTheme,roomRate,hotelRate);

            // then
            assertEquals(response.imageUrl(), "image");
        }

        @Test
        @DisplayName("주말 호텔 성급 및 리뷰들이 나온다")
        void getHotelRateAndReviewRatesOnWeekend() {
            //given
            List<Product> products = createMockProducts();
            Product product = products.get(0);
            RoomTheme roomTheme = product.getReservation().getHotel().getRoom().getRoomTheme();
            String roomRate = product.getReservation().getHotel().getRoom().getRoomAllRating();
            String hotelRate = product.getReservation().getHotel().getHotelLevel();

            //when
            WeekendProductResponse response =
                WeekendMapper.toWeekendProductResponse(product, product.getReservation(),
                    "image", 200000, 0.6, 2, roomTheme, roomRate, hotelRate);


            //then
            assertEquals(response.reviewRate(),"4.5");
            assertEquals(response.hotelRate(),"5성급");
            assertNotEquals(response.reviewRate(), "5.0");
            assertNotEquals(response.hotelRate(), "4성급");


        }
    }


}
