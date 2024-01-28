package com.yanolja.scbj.domain.product.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.product.controller.ProductRestController;
import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.request.ProductSearchRequest;
import com.yanolja.scbj.domain.product.dto.response.CityResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductMainResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.dto.response.ProductSearchResponse;
import com.yanolja.scbj.domain.product.dto.response.WeekendProductResponse;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;

public class ProductRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    private ProductService productService;

    @MockBean
    private SecurityUtil securityUtil;

    @MockBean
    private AlarmService alarmService;

    @Override
    public Object initController() {
        return new ProductRestController(productService, securityUtil);
    }

    @Test
    @DisplayName("상품(양도글) 작성 API 문서화")
    void postProduct() throws Exception {
        // given
        ProductPostRequest productPostRequest = ProductPostRequest.builder()
            .firstPrice(250000)
            .secondPrice(200000)
            .bank("신한은행")
            .accountNumber("110-499-519198")
            .secondGrantPeriod(5)
            .isRegistered(true)
            .standardTimeSellingPolicy(true)
            .totalAmountPolicy(true)
            .sellingModificationPolicy(true)
            .productAgreement(true)
            .build();

        ProductPostResponse productPostResponse = ProductPostResponse.builder()
            .productId(1L)
            .build();

        given(productService.postProduct(any(Long.TYPE), any(Long.TYPE),
            any(ProductPostRequest.class))).willReturn(productPostResponse);

        // when, then
        mockMvc.perform(post("/v1/products/{reservation_id}", 1L)
                .content(objectMapper.writeValueAsString(productPostRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andDo(restDoc.document(
                pathParameters(parameterWithName("reservation_id").description("예약내역 식별자")),
                requestFields(
                    fieldWithPath("firstPrice").type(JsonFieldType.NUMBER).description("1차 양도 가격"),
                    fieldWithPath("secondPrice").type(JsonFieldType.NUMBER).description("2차 양도 가격"),
                    fieldWithPath("bank").type(JsonFieldType.STRING).description("정산 은행"),
                    fieldWithPath("accountNumber").type(JsonFieldType.STRING).description("정산 계좌"),
                    fieldWithPath("secondGrantPeriod").type(JsonFieldType.NUMBER)
                        .description("2차 양도 시점"),
                    fieldWithPath("isRegistered").type(JsonFieldType.BOOLEAN)
                        .description("2차 양도 가격 설정 여부"),
                    fieldWithPath("standardTimeSellingPolicy").type(JsonFieldType.BOOLEAN)
                        .description("체크인 기준 판매 자동 완료 방침"),
                    fieldWithPath("totalAmountPolicy").type(JsonFieldType.BOOLEAN)
                        .description("정산 총액 확인 방침"),
                    fieldWithPath("sellingModificationPolicy").type(JsonFieldType.BOOLEAN)
                        .description("판매가 수정 불가 방침"),
                    fieldWithPath("productAgreement").type(JsonFieldType.BOOLEAN)
                        .description("판매 진행 동의 방침")
                ),
                responseFields(responseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.productId").type(JsonFieldType.NUMBER).description("상품 식별자")
                )));
    }

    @Test
    @DisplayName("상품 상세 조회 API 문서화")
    void getDetailProduct() throws Exception {
        // given
        Room room = Room.builder()
            .roomName("스탠다드 더블")
            .bedType("킹")
            .standardPeople(2)
            .maxPeople(4)
            .checkIn(LocalTime.now())
            .checkOut(LocalTime.now().plusHours(40))
            .build();

        Hotel hotel = Hotel.builder()
            .hotelName("호텔인 나인 강남")
            .room(room)
            .build();

        RoomThemeFindResponse roomThemeResponse = RoomThemeFindResponse.builder()
            .parkingZone(true)
            .oceanView(false)
            .pool(true)
            .breakfast(false)
            .build();

        ProductFindResponse findResponse = ProductFindResponse.builder()
            .hotelName(hotel.getHotelName())
            .hotelImageUrlList(List.of(
                "https://yaimg.yanolja.com/v5/2023/03/23/15/1280/641c76db5ab761.18136153.jpg"))
            .roomName(room.getRoomName())
            .checkIn(LocalDateTime.now())
            .checkOut(LocalDateTime.now())
            .originalPrice(200000)
            .sellingPrice(100000)
            .standardPeople(room.getStandardPeople())
            .maxPeople(room.getMaxPeople())
            .bedType(room.getBedType())
            .roomTheme(roomThemeResponse)
            .hotelAddress("서울특별시 강남구 테헤란로 99길 9")
            .hotelInfoUrl("https://place-site.yanolja.com/places/3001615")
            .saleStatus(true)
            .isSeller(false)
            .build();

        given(productService.findProduct(any())).willReturn(findResponse);

        // when, then
        mockMvc.perform(get("/v1/products/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDoc.document(
                pathParameters(parameterWithName("productId").description("상품 식별자")),
                responseFields(this.responseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).optional()
                        .description("응답 데이터"),
                    fieldWithPath("data.hotelName").type(JsonFieldType.STRING).description("호텔 이름"),
                    fieldWithPath("data.hotelImageUrlList[]").type(JsonFieldType.ARRAY)
                        .description("호텔 사진 리스트"),
                    fieldWithPath("data.roomName").type(JsonFieldType.STRING).description("객실 명"),
                    fieldWithPath("data.checkIn").type(JsonFieldType.STRING).description("체크인"),
                    fieldWithPath("data.checkOut").type(JsonFieldType.STRING).description("체크아웃"),
                    fieldWithPath("data.originalPrice").type(JsonFieldType.NUMBER)
                        .description("원가"),
                    fieldWithPath("data.sellingPrice").type(JsonFieldType.NUMBER)
                        .description("판매가"),
                    fieldWithPath("data.standardPeople").type(JsonFieldType.NUMBER)
                        .description("기준 인원"),
                    fieldWithPath("data.maxPeople").type(JsonFieldType.NUMBER).description("최대 인원"),
                    fieldWithPath("data.bedType").type(JsonFieldType.STRING).description("침대 타입"),
                    fieldWithPath("data.roomTheme").type(JsonFieldType.OBJECT).description("객실 테마"),
                    fieldWithPath("data.roomTheme.parkingZone").type(JsonFieldType.BOOLEAN)
                        .description("주차 가능"),
                    fieldWithPath("data.roomTheme.breakfast").type(JsonFieldType.BOOLEAN)
                        .description("조식 제공"),
                    fieldWithPath("data.roomTheme.pool").type(JsonFieldType.BOOLEAN)
                        .description("수영장"),
                    fieldWithPath("data.roomTheme.oceanView").type(JsonFieldType.BOOLEAN)
                        .description("오션 뷰"),
                    fieldWithPath("data.hotelAddress").type(JsonFieldType.STRING)
                        .description("호텔 주소"),
                    fieldWithPath("data.hotelInfoUrl").type(JsonFieldType.STRING)
                        .description("호텔 상세 정보 Url"),
                    fieldWithPath("data.saleStatus").type(JsonFieldType.BOOLEAN)
                        .description("판매 상태"),
                    fieldWithPath("data.isSeller").type(JsonFieldType.BOOLEAN).description("판매자 여부")
                )
            ));
    }

    @Test
    @DisplayName("상품 삭제 API 문서화")
    void deleteProduct() throws Exception {
        // given
        doNothing().when(productService).deleteProduct(any());

        // when, then
        mockMvc.perform(delete("/v1/products/{productId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent())
            .andDo(restDoc.document(
                pathParameters(parameterWithName("productId").description("상품 식별자")),
                responseFields(this.responseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).optional()
                        .description("응답 데이터")
                )
            ));
    }

    @Test
    @DisplayName("메인 페이지 API 문서화")
    void MainPageProduct() throws Exception {

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
        Page<WeekendProductResponse>
            weekendPage =
            new PageImpl<>(List.of(weekendProductResponse), pageable, 1); // 인자를 하나만 줬을때 문제다?

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
        ResultActions result = mockMvc.perform(get("/v1/products/main")
            .contentType(MediaType.APPLICATION_JSON)
            .param("size", "10")
            .param("cityNames", "서울", "강원", "부산", "제주", "경상", "전라")
            .param("page", "1")
        );

        // then
        result.andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.valueOf("application/json;charset=UTF-8")))
            .andExpect(jsonPath("$.data").exists())
            .andDo(restDoc.document(
                responseFields(
                    fieldWithPath("message").description("응답 메시지"),
                    subsectionWithPath("data").description("메인 페이지 상품 데이터"),
                    subsectionWithPath("data.seoul").description("서울 지역 상품 목록"),
                    subsectionWithPath("data.gangwon").description("강원 지역 상품 목록"),
                    subsectionWithPath("data.busan").description("부산 지역 상품 목록"),
                    subsectionWithPath("data.weekend").description("주말 특가 상품 목록")
                )
            ));
    }

    @Test
    @DisplayName("검색 조회 API 문서화")
    void _will_success() throws Exception {
        // given
        ProductSearchRequest searchRequest =
            ProductSearchRequest.builder()
                .build();

        ProductSearchResponse response = ProductSearchResponse.builder()
            .id(1L)
            .checkIn(LocalDateTime.now().plusDays(1).toLocalDate())
            .checkOut(LocalDateTime.now().plusDays(2).toLocalDate())
            .salePrice(100000)
            .name("시그니엘 레지던스 호텔")
            .build();

        Pageable pageable = PageRequest.of(1, 10);

        Page<ProductSearchResponse> expectedResponse =
            new PageImpl<>(List.of(response), pageable, 1);
        when(productService.searchByRequest(any(ProductSearchRequest.class), eq(pageable)))
            .thenReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/v1/products/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest))
                .param("page", "1"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(restDoc.document(
                requestFields(
                    fieldWithPath("location").description("검색할 지역").optional(),
                    fieldWithPath("checkIn").description("체크인 날짜").optional(),
                    fieldWithPath("checkOut").description("체크아웃 날짜").optional(),
                    fieldWithPath("quantityPeople").description("인원 수").optional(),
                    fieldWithPath("sorted").description("정렬 방식").optional(),
                    fieldWithPath("parking").description("주차 가능 여부").optional(),
                    fieldWithPath("brunch").description("브런치 제공 여부").optional(),
                    fieldWithPath("pool").description("수영장 유무").optional(),
                    fieldWithPath("oceanView").description("오션뷰 유무").optional()
                ),
                responseFields(
                    fieldWithPath("data.content[].roomType").description("방 타입").optional(),
                    fieldWithPath("data.content[].imageUrl").description("호텔 이미지 URL").optional(),
                    fieldWithPath("data.content[].originalPrice").description("원래 가격").optional(),
                    fieldWithPath("data.content[].isFirstPrice").description("첫 번째 가격 여부").optional(),
                    fieldWithPath("data.content[].salePercentage").description("세일 비율").optional(),
                    fieldWithPath("data.content[].createdAt").description("생성 시간").optional(),
                    fieldWithPath("message").description("Response message"),
                    fieldWithPath("data.content[].id").description("상품 ID"),
                    fieldWithPath("data.content[].checkIn").description("체크인 날짜"),
                    fieldWithPath("data.content[].checkOut").description("체크아웃 날짜"),
                    fieldWithPath("data.content[].salePrice").description("할인된 가격"),
                    fieldWithPath("data.content[].name").description("상품 이름"),
                    fieldWithPath("data.pageable.pageNumber").description("페이지 번호"),
                    fieldWithPath("data.pageable.pageSize").description("페이지 크기"),
                    fieldWithPath("data.pageable.sort").description("정렬 정보"),
                    fieldWithPath("data.last").description("마지막 페이지 여부"),
                    fieldWithPath("data.totalPages").description("총 페이지 수"),
                    fieldWithPath("data.totalElements").description("총 요소 수"),
                    fieldWithPath("data.size").description("페이지 크기"),
                    fieldWithPath("data.number").description("페이지 번호"),
                    fieldWithPath("data.first").description("첫 페이지 여부"),
                    fieldWithPath("data.numberOfElements").description("현재 페이지 요소 수"),
                    fieldWithPath("data.empty").description("페이지가 비어 있는지 여부"),
                    fieldWithPath("data.pageable.sort.empty").description("페이지 비어있음"),
                    fieldWithPath("data.pageable.sort.unsorted").description("페이지 정렬 불가"),
                    fieldWithPath("data.pageable.sort.sorted").description("페이지 정렬 가능"),
                    fieldWithPath("data.pageable.offset").description("페이지 사이즈"),
                    fieldWithPath("data.pageable.paged").description("페이지 유무"),
                    fieldWithPath("data.pageable.unpaged").description("페이지 사이즈"),
                    fieldWithPath("data.sort.empty").description("정렬 비어있는 기준"),
                    fieldWithPath("data.sort.sorted").description("정렬 기준"),
                    fieldWithPath("data.sort.unsorted").description("정렬 기준")
                )
            ));
    }
}