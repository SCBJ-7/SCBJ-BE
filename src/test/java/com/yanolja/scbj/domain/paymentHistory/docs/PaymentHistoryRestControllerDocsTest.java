package com.yanolja.scbj.domain.paymentHistory.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.paymentHistory.controller.PaymentHistoryRestController;
import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentSuccessResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PreparePaymentResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificSaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentHistoryService;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentService;
import com.yanolja.scbj.domain.paymentHistory.service.paymentApi.KaKaoPaymentService;
import com.yanolja.scbj.domain.paymentHistory.service.paymentApi.PaymentApiService;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class PaymentHistoryRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    private PaymentService paymentService;

    @MockBean
    PaymentHistoryService paymentHistoryService;

    @MockBean
    private Map<String, PaymentApiService> paymentApiServiceMap;

    @MockBean
    private KaKaoPaymentService kaKaoPaymentService;

    @MockBean
    SecurityUtil securityUtil;

    @MockBean
    AlarmService alarmService;

    @Override
    public Object initController() {
        return new PaymentHistoryRestController(paymentHistoryService, securityUtil);
    }

    @Test
    @DisplayName("getSpecificPurchasedHistory()는 구매내역을 상세 조회할 수 있다.")
    void getSpecificPurchasedHistory() throws Exception {
        // given
        SpecificPurchasedHistoryResponse specificPurchasedHistoryResponse =
            SpecificPurchasedHistoryResponse.builder()
                .hotelName("SR 호텔 서울 마곡")
                .roomName("체크인 시 배정")
                .standardPeople(2)
                .maxPeople(4)
                .checkIn("24.02.06 (일) 15:00")
                .checkOut("24.02.08 (월) 11:00")
                .customerName("김호텔")
                .customerPhoneNumber("010-1234-5678")
                .paymentHistoryId(1L)
                .paymentType("카카오페이")
                .originalPrice(216000)
                .price(180000)
                .remainingDays(10)
                .paymentHistoryDate("24.01.26 (금)")
                .hotelImage(
                    "https://yaimg.yanolja.com/v5/2022/09/20/13/1280/6329c608da8fb4.46198346.jpg")
                .build();

        given(paymentHistoryService.getSpecificPurchasedHistory(any(Long.TYPE),
            any(Long.TYPE))).willReturn(specificPurchasedHistoryResponse);

        // when, then
        mockMvc.perform(get("/v1/members/purchased-history/{paymentHistory_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDoc.document(
                pathParameters(parameterWithName("paymentHistory_id").description("구매내역 식별자")),
                responseFields(responseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.hotelName").type(JsonFieldType.STRING)
                        .description("호텔 이름"),
                    fieldWithPath("data.roomName").type(JsonFieldType.STRING)
                        .description("객실 이름"),
                    fieldWithPath("data.standardPeople").type(JsonFieldType.NUMBER)
                        .description("기준 인원"),
                    fieldWithPath("data.maxPeople").type(JsonFieldType.NUMBER)
                        .description("최대 인원"),
                    fieldWithPath("data.checkIn").type(JsonFieldType.STRING).description("체크인"),
                    fieldWithPath("data.checkOut").type(JsonFieldType.STRING).description("체크아웃"),
                    fieldWithPath("data.customerName").type(JsonFieldType.STRING)
                        .description("이용자 이름"),
                    fieldWithPath("data.customerPhoneNumber").type(JsonFieldType.STRING)
                        .description("이용자 번호"),
                    fieldWithPath("data.paymentHistoryId").type(JsonFieldType.NUMBER)
                        .description("예약 번호"),
                    fieldWithPath("data.paymentType").type(JsonFieldType.STRING)
                        .description("결제 수단"),
                    fieldWithPath("data.originalPrice").type(JsonFieldType.NUMBER)
                        .description("정가"),
                    fieldWithPath("data.price").type(JsonFieldType.NUMBER)
                        .description("구매가"),
                    fieldWithPath("data.remainingDays").type(JsonFieldType.NUMBER)
                        .description("남은 날짜"),
                    fieldWithPath("data.paymentHistoryDate").type(JsonFieldType.STRING)
                        .description("남은 시간"),
                    fieldWithPath("data.hotelImage").type(JsonFieldType.STRING)
                        .description("이미지")
                )
            ));
    }

    @Test
    @DisplayName("결제 페이지 조회 API 문서화")
    void findPaymentPage() throws Exception {
        // given
        PaymentPageFindResponse findResponse = PaymentPageFindResponse.builder()
            .hotelName("양도 호텔")
            .hotelImage(
                "https://yaimg.yanolja.com/v5/2023/03/23/15/1280/641c76db5ab761.18136153.jpg")
            .roomName("호텔 인 나인 강남")
            .standardPeople(2)
            .maxPeople(4)
            .checkInDateTime(LocalDateTime.now())
            .checkOutDateTime(LocalDateTime.now().plusDays(1))
            .originalPrice(200000)
            .salePrice(150000)
            .build();

        given(paymentService.getPaymentPage(1L)).willReturn(findResponse);

        // when
        mockMvc.perform(get("/v1/products/{product_id}/payments", 1L))
            .andDo(restDoc.document(
                pathParameters(parameterWithName("product_id").description("상품 식별자")),
                responseFields(responseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.hotelImage").type(JsonFieldType.STRING)
                        .description("호텔 이미지"),
                    fieldWithPath("data.hotelName").type(JsonFieldType.STRING).description("호텔 이름"),
                    fieldWithPath("data.roomName").type(JsonFieldType.STRING).description("객실 이름"),
                    fieldWithPath("data.standardPeople").type(JsonFieldType.NUMBER)
                        .description("기준 인원"),
                    fieldWithPath("data.maxPeople").type(JsonFieldType.NUMBER).description("최대 인원"),
                    fieldWithPath("data.checkInDateTime").type(JsonFieldType.STRING)
                        .description("체크 인"),
                    fieldWithPath("data.checkOutDateTime").type(JsonFieldType.STRING)
                        .description("체크 아웃"),
                    fieldWithPath("data.originalPrice").type(JsonFieldType.NUMBER)
                        .description("원가"),
                    fieldWithPath("data.salePrice").type(JsonFieldType.NUMBER).description("판매가")
                )));
    }


    @Test
    @DisplayName("결제 요청 API 문서화")
    void preparePayment() throws Exception {
        // given
        PaymentReadyRequest paymentReadyRequest = PaymentReadyRequest.builder()
            .customerName("김양도")
            .customerEmail("email@naver.com")
            .customerPhoneNumber("010-1234-1234")
            .isAgeOver14(true)
            .useAgree(true)
            .collectPersonalInfo(true)
            .cancelAndRefund(true)
            .thirdPartySharing(true)
            .build();

        PreparePaymentResponse preparePaymentResponse = PreparePaymentResponse.builder()
            .url("https://percenthotel.web.app/payment/66/cancel")
            .build();

        given(paymentApiServiceMap.get(any())).willReturn(kaKaoPaymentService);
        given(kaKaoPaymentService.preparePayment(any(), any())).willReturn(preparePaymentResponse);

        // when, then
        mockMvc.perform(post("/v1/products/{product_id}/payments?paymentType={paymentType}", 1L, "kakaoPaymentService")
                .content(objectMapper.writeValueAsString(paymentReadyRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDoc.document(
                pathParameters(parameterWithName("product_id").description("상품 식별자")),
                queryParameters(parameterWithName("paymentType").description("결제 타입")),
                requestFields(
                    fieldWithPath("customerName").type(JsonFieldType.STRING).description("구매자 명"),
                    fieldWithPath("customerEmail").type(JsonFieldType.STRING)
                        .description("구매자 이메일"),
                    fieldWithPath("customerPhoneNumber").type(JsonFieldType.STRING)
                        .description("구매자 전화번호"),
                    fieldWithPath("isAgeOver14").type(JsonFieldType.BOOLEAN)
                        .description("만 14세 이상 이용 동의"),
                    fieldWithPath("useAgree").type(JsonFieldType.BOOLEAN).description("이용규칙 동의"),
                    fieldWithPath("collectPersonalInfo").type(JsonFieldType.BOOLEAN)
                        .description("개인정 수집 및 이용 동의"),
                    fieldWithPath("cancelAndRefund").type(JsonFieldType.BOOLEAN)
                        .description("취소 및 환불 규칙 동의"),
                    fieldWithPath("thirdPartySharing").type(JsonFieldType.BOOLEAN)
                        .description("개인정보 제 3자 제공 동의")
                ),
                responseFields(responseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                    fieldWithPath("data.url").type(JsonFieldType.STRING).description("카카오페이 결제 url")
                )));
    }

    @Test
    @DisplayName("결제 성공 API 문서화")
    void successPayment() throws Exception {
        // given
        Member member = Member.builder()
            .id(1L)
            .build();
        String pg_token = "pg_token1234567890";
        PaymentSuccessResponse paymentSuccessResponse = PaymentSuccessResponse.builder()
            .paymentHistoryId(1L)
            .build();

        given(paymentApiServiceMap.get(any())).willReturn(kaKaoPaymentService);
        given(kaKaoPaymentService.approvePayment(any(), any())).willReturn(
            paymentSuccessResponse);

        // when, then
        mockMvc.perform(get("/v1/products/pay-success?memberId=" + member.getId()
                + "&paymentType=kakaoPaymentService&pg_token=" + pg_token)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDoc.document());
    }

    @Test
    @DisplayName("결제 실패 API 문서화")
    void cancelPayment() throws Exception {
        // given
        Member member = Member.builder()
            .id(1L)
            .build();

        given(paymentApiServiceMap.get(any())).willReturn(kaKaoPaymentService);
        doNothing().when(kaKaoPaymentService).cancelPayment();

        // when, then
        mockMvc.perform(get("/v1/products/pay-cancel?memberId=" + member.getId()
                + "&paymentType=kakaoPaymentService")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDoc.document());
    }

    @Test
    @DisplayName("구매 내역 목록 조회 API 문서화")
    void getPurchasedHistory() throws Exception {
        // given
        List<PurchasedHistoryResponse> responses = List.of(
            new PurchasedHistoryResponse(1L, LocalDateTime.now(), "wwww.yanolja.com", "A 호텔",
                "디럭스", 20000,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2)),
            new PurchasedHistoryResponse(2L, LocalDateTime.now().minusDays(3),
                "wwww.yanolja.com", "B 호텔", "스텐다드",
                15000, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1))
        );
        // when
        given(paymentHistoryService.getUsersPurchasedHistory(anyLong())).willReturn(responses);

        // then
        mockMvc.perform(get("/v1/members/purchased-history")
                .param("page", "0")
                .param("pageSize", "10"))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("payment-history/purchase-history",
                responseFields(
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data[].id").type(JsonFieldType.NUMBER).description("구매 내역 식별자"),
                    fieldWithPath("data[].createdAt").type(JsonFieldType.STRING).description("생성 날짜 및 시간"),
                    fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING).description("이미지 URL"),
                    fieldWithPath("data[].name").type(JsonFieldType.STRING).description("호텔/객실 이름"),
                    fieldWithPath("data[].roomType").type(JsonFieldType.STRING).description("객실 유형"),
                    fieldWithPath("data[].price").type(JsonFieldType.NUMBER).description("가격"),
                    fieldWithPath("data[].checkInDate").type(JsonFieldType.STRING).description("체크인 날짜 및 시간"),
                    fieldWithPath("data[].checkOutDate").type(JsonFieldType.STRING).description("체크아웃 날짜 및 시간")
                )
            ));
    }

    @Test
    @DisplayName("판매 내역 목록 조회 API 문서화")
    void getSaleHistories() throws Exception {
        // given
        List<SaleHistoryResponse> responses = List.of(new SaleHistoryResponse(
            1L,
            10L,
            "롯데 시그니엘 호텔",
            "http://example.com/hotel-room-image1.jpg",
            "더블 베드",
            200000,
            100000,
            LocalDateTime.of(2024, 1, 1, 15, 0),
            LocalDateTime.of(2024, 1, 2, 1, 0),
            "판매중"
        ), new SaleHistoryResponse(
            10L,
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

        // when & then
        mockMvc.perform(get("/v1/members/sale-history")
                .param("page", "0")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDoc.document(
                responseFields(this.responseCommon()).andWithPrefix("data[].",
                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("판매 내역 식별자"),
                    fieldWithPath("productId").type(JsonFieldType.NUMBER).description("상품 id"),
                    fieldWithPath("name").type(JsonFieldType.STRING).description("호텔 이름"),
                    fieldWithPath("imageUrl").type(JsonFieldType.STRING).description("호텔 이미지 URL"),
                    fieldWithPath("roomType").type(JsonFieldType.STRING).description("객실 유형"),
                    fieldWithPath("firstPrice").type(JsonFieldType.NUMBER).description("1차 판매가"),
                    fieldWithPath("secondPrice").type(JsonFieldType.NUMBER).description("2차 판매가"),
                    fieldWithPath("checkInDate").type(JsonFieldType.STRING).description("체크인 날짜"),
                    fieldWithPath("checkOutDate").type(JsonFieldType.STRING).description("체크아웃 날짜"),
                    fieldWithPath("saleStatus").type(JsonFieldType.STRING).description("판매 상태")
                )
            ));
    }

    @Test
    @DisplayName("판매내역 상세 조회 API 문서화")
    public void testGetSpecificSaleHistory() throws Exception {
        //given
        Long memberId = 1L;
        Long saleHistoryId = 1L;
        boolean isPaymentId = true;


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

        given(paymentHistoryService.getSpecificSaleHistory(anyLong(),anyLong(),anyBoolean())).willReturn(response);

        //When& Then
            mockMvc.perform(get("/v1/members/sale-history/{saleHistory_id}/{isPaymentId}", saleHistoryId, isPaymentId))
            .andExpect(status().isOk())
            .andDo(print())
            .andDo(document("payment-history/specific-sale-history",
                pathParameters(
                    parameterWithName("saleHistory_id").description("판매 내역 식별자"),
                    parameterWithName("isPaymentId").description("결제 ID 여부")
                ),
                responseFields(
                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메시지"),
                    fieldWithPath("data.saleStatus").type(JsonFieldType.STRING).description("판매 상태"),
                    fieldWithPath("data.checkIn").type(JsonFieldType.STRING).description("체크인 시간"),
                    fieldWithPath("data.checkOut").type(JsonFieldType.STRING).description("체크아웃 시간"),
                    fieldWithPath("data.hotelImage").type(JsonFieldType.STRING).description("호텔 이미지"),
                    fieldWithPath("data.standardPeople").type(JsonFieldType.NUMBER).description("기준 인원"),
                    fieldWithPath("data.maxPeople").type(JsonFieldType.NUMBER).description("최대 인원"),
                    fieldWithPath("data.hotelName").type(JsonFieldType.STRING).description("호텔 이름"),
                    fieldWithPath("data.roomName").type(JsonFieldType.STRING).description("객실 이름"),
                    fieldWithPath("data.bank").type(JsonFieldType.STRING).description("은행 이름"),
                    fieldWithPath("data.accountNumber").type(JsonFieldType.STRING).description("계좌 번호"),
                    fieldWithPath("data.firstPrice.originalPrice").type(JsonFieldType.NUMBER).description("원래 가격"),
                    fieldWithPath("data.firstPrice.firstSalePrice").type(JsonFieldType.NUMBER).description("첫 번째 판매 가격"),
                    fieldWithPath("data.secondPrice.secondPriceStartDate").type(JsonFieldType.STRING).description("두 번째 가격 시작 날짜"),
                    fieldWithPath("data.secondPrice.secondPrice").type(JsonFieldType.NUMBER).description("두 번째 가격"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 시간")
                )
            ));
    }
}
