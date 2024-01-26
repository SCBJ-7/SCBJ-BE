package com.yanolja.scbj.domain.product.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.product.controller.ProductRestController;
import com.yanolja.scbj.domain.product.dto.response.ProductFindResponse;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class ProductRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    private ProductService productService;

    @MockBean
    private SecurityUtil securityUtil;

    @Override
    public Object initController() {
        return new ProductRestController(productService, securityUtil);
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
}