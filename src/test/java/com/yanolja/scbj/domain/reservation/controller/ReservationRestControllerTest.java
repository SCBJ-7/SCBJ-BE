package com.yanolja.scbj.domain.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.service.ReservationService;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = ReservationRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class ReservationRestControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private SecurityUtil securityUtil;

    private Room room;
    private Hotel hotel;
    private Reservation reservation;
    private HotelRoomImage hotelRoomImage;

    @BeforeEach
    private void init() {
        room = Room.builder()
            .roomName("페밀리")
            .checkIn(LocalTime.now())
            .checkOut(LocalTime.now())
            .bedType("싱글")
            .standardPeople(2)
            .maxPeople(4)
            .roomTheme(RoomTheme.builder().build())
            .build();

        hotelRoomImage = HotelRoomImage.builder()
            .url("image1")
            .build();

        hotel = Hotel.builder()
            .id(1L)
            .hotelName("테스트 호텔")
            .hotelMainAddress("서울")
            .hotelDetailAddress("서울광역시 강남구")
            .hotelInfoUrl("vasnoanwfowiamsfokm.jpg")
            .room(room)
            .hotelRoomImageList(List.of(hotelRoomImage))
            .build();

        reservation = Reservation.builder()
            .id(1L)
            .hotel(hotel)
            .startDate(LocalDateTime.now())
            .endDate(LocalDateTime.now())
            .purchasePrice(2500000)
            .build();
    }

    @Nested
    @DisplayName("findReservation()는 ")
    class Context_findReservation {

        @Test
        @DisplayName("예약 내역 조회에 성공했습니다.")
        void _willSuccess() throws Exception {
            // given
            List<ReservationFindResponse> findResponse = new ArrayList<>();
            ReservationFindResponse reservationFindResponse = ReservationFindResponse.builder()
                .reservationId(reservation.getId())
                .hotelName(hotel.getHotelName())
                .imageUrl(hotel.getHotelRoomImageList().get(0).getUrl())
                .roomName(room.getRoomName())
                .startDate(reservation.getStartDate())
                .endDate(reservation.getEndDate())
                .purchasePrice(reservation.getPurchasePrice())
                .remainingDays(
                    (int) ChronoUnit.DAYS.between(LocalDate.now(), reservation.getStartDate()))
                .remainingTimes(LocalDateTime.now().getHour() - room.getCheckIn().getHour())
                .build();

            findResponse.add(reservationFindResponse);

            given(reservationService.getReservation(any(Long.TYPE))).willReturn(findResponse);

            // when, then
            mvc.perform(get("/v1/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists()).andDo(print());
        }
    }
}