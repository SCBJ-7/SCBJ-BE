package com.yanolja.scbj.domain.product.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


@WebMvcTest(
    controllers = ProductRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class ProductRestControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ProductService productService;

    @MockBean
    private SecurityUtil securityUtil;

    private Room room;
    private Hotel hotel;

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

        hotel = Hotel.builder()
            .id(1L)
            .hotelName("테스트 호텔")
            .hotelMainAddress("서울")
            .hotelDetailAddress("서울광역시 강남구")
            .hotelInfoUrl("vasnoanwfowiamsfokm.jpg")
            .room(room)
            .build();
    }

    @Nested
    @DisplayName("postProduct()는 ")
    class Context_postProduct {

        @Test
        @DisplayName("2차 가격이 있는 양도글 작성을 성공했습니다.")
        void _willSuccesswithSecond() throws Exception {
            // given
            long reservationId = 1L;
            ProductPostResponse productPostResponse = ProductPostResponse.builder()
                .productId(1L)
                .build();
            ProductPostRequest productPostRequestwithSecond = ProductPostRequest.builder()
                .firstPrice(350000)
                .secondPrice(20000)
                .bank("신한은행").accountNumber("1000-4400-3330")
                .secondGrantPeriod(48)
                .build();
            given(productService.postProduct(any(Long.TYPE), any(Long.TYPE),
                any(ProductPostRequest.class))).willReturn(productPostResponse);

            // when, then
            mvc.perform(post("/v1/products/{reservation_id}", reservationId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productPostRequestwithSecond)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists()).andDo(print());
        }

        @Test
        @DisplayName("2차 가격이 없는 양도글 작성을 성공했습니다.")
        void _willSuccesswithoutSecond() throws Exception {
            // given
            long reservationId = 1L;
            ProductPostResponse productPostResponse = ProductPostResponse.builder()
                .productId(1L)
                .build();
            ProductPostRequest productPostRequestwithoutSecond = ProductPostRequest.builder()
                .firstPrice(350000)
                .bank("신한은행").accountNumber("1000-4400-3330")
                .build();
            given(productService.postProduct(any(Long.TYPE), any(Long.TYPE),
                any(ProductPostRequest.class))).willReturn(productPostResponse);

            // when, then
            mvc.perform(post("/v1/products/{reservation_id}", reservationId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productPostRequestwithoutSecond)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists()).andDo(print());
        }
    }


    @Nested
    @DisplayName("상품 상세 조회는")
    class Context_findProduct {

        @Test
        @DisplayName("성공시 호텔 이름과 원가를 함께 반환한다.")
        void _will_success() throws Exception {
            // given
            Long targetProductId = 1L;
            ProductFindResponse findResponse = ProductFindResponse.builder()
                .hotelName(hotel.getHotelName())
                .roomName(room.getRoomName())
                .checkIn(LocalDateTime.now())
                .checkOut(LocalDateTime.now())
                .originalPrice(200000)
                .sellingPrice(100000)
                .standardPeople(room.getStandardPeople())
                .maxPeople(room.getMaxPeople())
                .bedType(room.getBedType())
                .build();

            given(productService.findProduct(any())).willReturn(findResponse);

            // when
            ResultActions response = mvc.perform(get("/v1/products/" + targetProductId));

            // then
            response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.hotelName", is(findResponse.hotelName())));

        }
    }

    @Nested
    @DisplayName("상품 삭제는 ")
    class Context_deleteProduct {

        @Test
        @DisplayName("성공 시 204가 반환된다.")
        void _will_success() throws Exception {
            // given
            Long targetProductId = 1L;

            doNothing().when(productService).deleteProduct(any());

            // when
            ResultActions response = mvc.perform(delete("/v1/products/" + targetProductId));

            // then
            response.andExpect(status().isNoContent())
                .andDo(print());
        }
    }
}