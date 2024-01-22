package com.yanolja.scbj.domain.paymentHistory.paymentController;

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
import com.yanolja.scbj.domain.paymentHistory.controller.PaymentHistoryRestController;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificSaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentHistoryService;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.nio.charset.StandardCharsets;
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
    controllers = PaymentHistoryRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class PaymentHistoryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentHistoryService paymentHistoryService;

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
            List<PurchasedHistoryResponse> responses = List.of(
                new PurchasedHistoryResponse(1L, LocalDateTime.now(), "wwww.yanolja.com", "A 호텔",
                    "디럭스", 20000,
                    LocalDateTime.now(), LocalDateTime.now().plusDays(2)),
                new PurchasedHistoryResponse(2L, LocalDateTime.now().minusDays(3),
                    "wwww.yanolja.com", "B 호텔", "스텐다드",
                    15000, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1))
            );

            given(paymentHistoryService.getUsersPurchasedHistory(
                anyLong())).willReturn(responses);

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
                100000,
                LocalDateTime.of(2024, 1, 1, 15, 0),
                LocalDateTime.of(2024, 1, 2, 1, 0),
                "판매중"
            ), new SaleHistoryResponse(
                2L,
                "신라 호텔",
                "http://example.com/hotel-room-image2.jpg",
                "트윈 베드",
                150000,
                80000,
                LocalDateTime.of(2024, 1, 3, 15, 0),
                LocalDateTime.of(2024, 1, 4, 11, 0),
                "거래완료"
            ));


            given(paymentHistoryService.getUsersSaleHistory(anyLong())).willReturn(responses);

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
            SpecificPurchasedHistoryResponse specificPurchasedHistoryResponse =
                SpecificPurchasedHistoryResponse.builder()
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

            given(paymentHistoryService.getSpecificPurchasedHistory(any(Long.TYPE),
                any(Long.TYPE))).willReturn(specificPurchasedHistoryResponse);

            // when, then
            mockMvc.perform(get("/v1/members/purchased-history/{paymentHistory_id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hotelName").exists()).andDo(print());


        }
    }

    @Nested
    @DisplayName("판매내역 상세 조회는")
    class Context_getSpecificSaleHistory {


        @Test
        public void testGetSpecificSaleHistory() throws Exception {
            //given
            Long memberId = 1L;
            Long saleHistoryId = 1L;

            SpecificSaleHistoryResponse.firstPriceResponse firstPriceObject =
                SpecificSaleHistoryResponse.firstPriceResponse.builder()
                    .originalPrice(212000)
                    .firstSalePrice(139000)
                    .build();


            SpecificSaleHistoryResponse.secondPriceResponse secondPriceObject =
                SpecificSaleHistoryResponse.secondPriceResponse.builder()
                    .secondPrice(20000)
                    .secondPriceStartDate("20203")
                    .build();

            SpecificSaleHistoryResponse response = SpecificSaleHistoryResponse.builder()
                .saleStatus("판매중")
                .checkIn("24.01.15 (월) 15:00")
                .checkOut("24.01.16 (화) 15:00")
                .hotelImage("image.url")
                .standardPeople(2)
                .maxPeople(4)
                .hotelName("호텔 인 나인 강남")
                .roomName("디럭스 킹 시티뷰")
                .bank("신한")
                .accountNumber("110472321")
                .firstPrice(firstPriceObject)
                .secondPrice(secondPriceObject)
                .createdAt(LocalDateTime.now().minusDays(6))
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(memberId);
            given(paymentHistoryService.getSpecificSaleHistory(memberId, saleHistoryId)).willReturn(
                response);

            // 실행 (When)
            mockMvc.perform(get("/v1/members/sale-history/" + saleHistoryId))
                .andExpect(status().isOk())
                .andDo(print())

                // 검증 (Then)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("판매 내역 상세 조회를 성공했습니다"))
                .andExpect(jsonPath("$.data").isNotEmpty()); // 추가적인 jsonPath 검증이 필요할 수 있음
        }
    }
}



