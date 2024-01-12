package com.yanolja.scbj.domain.payment.paymentController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.payment.controller.HistoryController;
import com.yanolja.scbj.domain.payment.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.payment.service.HistoryService;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@WebMvcTest(
    controllers = HistoryController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class HistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HistoryService historyService;

    @SpyBean
    private SecurityUtil securityUtil;

    private Room room;
    private Hotel hotel;
    private Reservation reservation;
    private HotelRoomImage hotelRoomImage;
    private PaymentHistory paymentHistory;

    @BeforeEach
    void setup() {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(1L, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

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

        paymentHistory = PaymentHistory.builder()
            .id(1L)
            .customerName("박아무개")
            .customerEmail("yang980329@naver.com")
            .customerPhoneNumber("010-0000-0000")
            .price(15000)
            .paymentType("카카오페이")
            .build();

    }

    @Nested
    @DisplayName("구매내역은")
    class Context_purchaseHistory {

        @Test
        @DisplayName("성공시 구매내역 리스트를 보여준다")
        void will_success() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<PurchasedHistoryResponse> responses = List.of(
                new PurchasedHistoryResponse(1L, LocalDateTime.now(), "wwww.yanolja.com", "A 호텔",
                    "디럭스", 20000,
                    LocalDate.now(), LocalDate.now().plusDays(2)),
                new PurchasedHistoryResponse(2L, LocalDateTime.now().minusDays(3),
                    "wwww.yanolja.com", "B 호텔", "스텐다드",
                    15000, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
            );
            Page<PurchasedHistoryResponse> response =
                new PageImpl<>(responses, pageable, responses.size());

            given(historyService.getUsersPurchasedHistory(any(Pageable.class),
                anyLong())).willReturn(response);

            // when
            MvcResult result = mockMvc.perform(get("/v1/members/purchased-history")
                    .param("page", "0")
                    .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

            // then
            String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            assertThat(content).contains("조회에 성공하였습니다.");
            assertThat(content).contains("A 호텔");
            assertThat(content).contains("디럭스");
        }
    }

    @Nested
    @DisplayName("판매내역은")
    class Context_saleHistory {

        @Test
        @DisplayName("성공시 구매내역 리스트를 보여준다")
        void will_success() throws Exception {
            //given
            Pageable pageable = PageRequest.of(0, 10);
            List<SaleHistoryResponse> responses = List.of(new SaleHistoryResponse(
                1L,
                "롯데 시그니엘 호텔",
                "http://example.com/hotel-room-image1.jpg",
                "더블 베드",
                200000,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2),
                "판매중"
            ), new SaleHistoryResponse(
                2L,
                "신라 호텔",
                "http://example.com/hotel-room-image2.jpg",
                "트윈 베드",
                150000,
                LocalDate.of(2024, 1, 3),
                LocalDate.of(2024, 1, 4),
                "거래완료"
            ));

            PageImpl<SaleHistoryResponse> saleHistoryResponses =
                new PageImpl<>(responses, pageable, responses.size());

            given(historyService.getUsersSaleHistory(any(Pageable.class), anyLong())).willReturn(
                saleHistoryResponses);

            //when
            MvcResult result = mockMvc.perform(get("/v1/members/sale-history")
                    .param("page", "0")
                    .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
            String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            assertThat(content).contains("조회에 성공하였습니다.");
            assertThat(content).contains("신라 호텔");
            assertThat(content).contains("판매중");
        }
    }

    @Nested
    @DisplayName("getSpecificPurchasedHistory()는")
    class Context_getSpecificPurchasedHistory {

        @Test
        @DisplayName("구매 내역 상세 조회를 성공했습니다.")
        void _willSuccess() throws Exception {
            // given
            SpecificPurchasedHistoryResponse specificPurchasedHistoryResponse = SpecificPurchasedHistoryResponse.builder()
                .hotelName(hotel.getHotelName())
                .roomName(room.getRoomName())
                .standardPeople(room.getStandardPeople())
                .maxPeople(room.getMaxPeople())
                .checkIn("23.12.24 (일) 15:00")
                .checkOut("23.12.25 (월) 11:00")
                .customerName(paymentHistory.getCustomerName())
                .customerPhoneNumber(paymentHistory.getCustomerPhoneNumber())
                .paymentHistoryId(paymentHistory.getId())
                .paymentType(paymentHistory.getPaymentType())
                .originalPrice(50000000)
                .price(paymentHistory.getPrice())
                .remainingDays(3)
                .paymentHistoryDate("23.12.17 (월)")
                .hotelImage(hotelRoomImage.getUrl())
                .build();

            given(historyService.getSpecificPurchasedHistory(any(Long.TYPE),
                any(Long.TYPE))).willReturn(specificPurchasedHistoryResponse);

            // when, then
            mockMvc.perform(get("/v1/members/purchased-history/{paymentHistory_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hotelName").exists()).andDo(print());


        }
    }
}



