package com.yanolja.scbj.domain.paymentHistory.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.paymentHistory.controller.PaymentHistoryRestController;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentHistoryService;
import com.yanolja.scbj.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class PaymentHistoryRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    PaymentHistoryService paymentHistoryService;

    @MockBean
    SecurityUtil securityUtil;

    @MockBean
    AlarmService alarmService;

    @Override
    public Object initController() {
        return new PaymentHistoryRestController(paymentHistoryService,securityUtil);
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
                .hotelImage("https://yaimg.yanolja.com/v5/2022/09/20/13/1280/6329c608da8fb4.46198346.jpg")
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
}
