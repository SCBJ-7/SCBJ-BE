package com.yanolja.scbj.domain.product.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.CityResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductMainResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductStockResponse;
import com.yanolja.scbj.domain.product.dto.response.WeekendProductResponse;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Nested
    @DisplayName("상품 검색은")
    class Context_searchProduct {

        @Test
        @DisplayName("성공시 200이 반환된다")
        void _will_success() throws Exception {
            // given
            ProductSearchRequest searchRequest =
                ProductSearchRequest.builder()
//                    .location("서울")
                    .build();

            ProductSearchResponse response = ProductSearchResponse.builder()
                .id(1L)
                .checkIn(LocalDateTime.now().plusDays(1))
                .checkOut(LocalDateTime.now().plusDays(2))
                .salePrice(100000)
                .name("시그니엘 레지던스 호텔")
                .build();

            Pageable pageable = PageRequest.of(1, 10);

            Page<ProductSearchResponse> expectedResponse =
                new PageImpl<>(List.of(response), pageable, 1);
            objectMapper.writeValueAsString(expectedResponse);
            when(productService.searchByRequest(any(ProductSearchRequest.class), eq(pageable)))
                .thenReturn(expectedResponse);

            // when & then
            mvc.perform(post("/v1/products/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(searchRequest))
                    .param("page", "1"))
                .andExpect(status().isOk()).andDo(print());

        }
    }

    @Nested
    @DisplayName("메인 페이지는")
    class Context_mainProduct{

        @Test
        @DisplayName("성공시 200을 가져온다")
        void getProductsForMainTest() throws Exception {
            // given
            List<String> cityNames = List.of("서울", "강원", "부산", "제주", "경상", "전라");

            CityResponse seoulCityResponse = CityResponse.builder()
                .id(1L)
                .city("서울")
                .imageUrl("image_url_seoul.jpg")
                .hotelName("서울 호텔")
                .roomType("더블")
                .originalPrice(200000)
                .salePrice(180000)
                .salePercentage(10.0)
                .checkInDate(LocalDateTime.now())
                .checkOutDate(LocalDateTime.now().plusDays(1))
                .build();

            WeekendProductResponse weekendProductResponse = WeekendProductResponse.builder()
                .id(2L)
                .hotelName("주말 호텔")
                .roomType("스위트")
                .imageUrl("image_url_weekend.jpg")
                .originalPrice(300000)
                .salePrice(270000)
                .salePercentage(10.0)
                .checkInDate(LocalDateTime.now().plusDays(5))
                .checkOutDate(LocalDateTime.now().plusDays(6))
                .isBrunchIncluded(true)
                .isPoolIncluded(false)
                .isOceanViewIncluded(true)
                .roomThemeCount(3)
                .build();

            Pageable pageable = PageRequest.of(1, 10);
            Page<WeekendProductResponse> weekendPage = new PageImpl<>(List.of(weekendProductResponse),pageable,1); // 인자를 하나만 줬을때 문제다?

            ProductMainResponse productMainResponse = ProductMainResponse.builder()
                .seoul(List.of(seoulCityResponse))
                .gangwon(List.of(seoulCityResponse))
                .busan(List.of(seoulCityResponse))
                .jeju(List.of(seoulCityResponse))
                .gyeongsang(List.of(seoulCityResponse))
                .jeolla(List.of(seoulCityResponse))
                .weekend(weekendPage)
                .build();
            objectMapper.writeValueAsString(weekendPage);
            when(productService.getAllProductForMainPage(any(), any(Pageable.class)))
                .thenReturn(productMainResponse);

            // when
            ResultActions result = mvc.perform(get("/v1/products/main")
                .contentType(MediaType.APPLICATION_JSON)
                .param("cityNames", "서울", "강원", "부산", "제주", "경상", "전라")
                .param("page", "1")
                .param("size", "10"));

            // then
            result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").exists());
        }

    }

    @Nested
    @DisplayName("상품 재고 조회는")
    class Context_getProductStock {

        @Test
        @DisplayName("재고가 존재시 true를 반환한다.")
        void will_success() throws Exception {
            // given
            long productId = 1L;
            ProductStockResponse productStockResponse = ProductStockResponse.builder()
                .hasStock(true).build();

            given(productService.isProductStockLeft(any(Long.TYPE))).willReturn(productStockResponse);

            // when, then
            mvc.perform(get("/v1/products/" + productId + "/stock"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.hasStock", is(productStockResponse.hasStock())));
        }
    }
}