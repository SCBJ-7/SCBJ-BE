package com.yanolja.scbj.domain.reservation.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.reservation.controller.ReservationRestController;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.service.ReservationService;
import com.yanolja.scbj.global.helper.TestConstants;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class ReservationRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    ReservationService reservationService;

    @MockBean
    SecurityUtil securityUtil;

    @MockBean
    AlarmService alarmService;

    @Override
    public Object initController() {
        return new ReservationRestController(reservationService, securityUtil);
    }

    @Test
    @DisplayName("getReservation()는 야놀자 예약내역을 확인할 수 있다.")
    void getReservation() throws Exception {
        // given
        List<ReservationFindResponse> reservationFindList = new ArrayList<>();
        reservationFindList.add(ReservationFindResponse.builder()
            .reservationId(31)
            .hotelName("SR 호텔 서울 마곡")
            .imageUrl("https://yaimg.yanolja.com/v5/2022/09/20/13/1280/6329c608da8fb4.46198346.jpg")
            .roomName("체크인 시 배정")
            .startDate(LocalDateTime.of(2024, 2, 6, 11, 0))
            .endDate(LocalDateTime.of(2024, 2, 8, 22, 0))
            .refundPrice(216000)
            .purchasePrice(216000)
            .remainingDays(10)
            .remainingTimes(260)
            .build());
        reservationFindList.add(ReservationFindResponse.builder()
            .reservationId(275)
            .hotelName("UH FLAT the 송도")
            .imageUrl("https://yaimg.yanolja.com/v5/2023/02/15/16/1280/63ed0f80af0cb7.13475065.png")
            .roomName("1C-1 TYPE 또는 1C TYPE 랜덤배정")
            .startDate(LocalDateTime.of(2024, 2, 19, 18, 0))
            .endDate(LocalDateTime.of(2024, 2, 21, 11, 0))
            .refundPrice(368000)
            .purchasePrice(368000)
            .remainingDays(24)
            .remainingTimes(279)
            .build());
        reservationFindList.add(ReservationFindResponse.builder()
            .reservationId(279)
            .hotelName("오크우드 프리미어 인천")
            .imageUrl("https://yaimg.yanolja.com/v5/2023/04/13/08/1280/6437c13b5d8c65.25779609.png")
            .roomName("스튜디오 슈페리어 룸")
            .startDate(LocalDateTime.of(2024, 2, 7, 15, 0))
            .endDate(LocalDateTime.of(2024, 2, 9, 11, 0))
            .refundPrice(279000)
            .purchasePrice(279000)
            .remainingDays(12)
            .remainingTimes(288)
            .build());
        given(reservationService.getReservation(any(Long.TYPE))).willReturn(reservationFindList);

        // when, then
        mockMvc.perform(get("/v1/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", TestConstants.ACCESS_TOKEN))
            .andExpect(status().isOk())
            .andDo(restDoc.document(
                jwtHeader(),
                responseFields(responseCommon()).and(
                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("응답 데이터"),
                    fieldWithPath("data[].reservationId").type(JsonFieldType.NUMBER)
                        .description("예약 식별자"),
                    fieldWithPath("data[].hotelName").type(JsonFieldType.STRING)
                        .description("호텔 이름"),
                    fieldWithPath("data[].imageUrl").type(JsonFieldType.STRING)
                        .description("이미지 url"),
                    fieldWithPath("data[].roomName").type(JsonFieldType.STRING)
                        .description("객실 이름"),
                    fieldWithPath("data[].startDate").type(JsonFieldType.STRING).description("시작일"),
                    fieldWithPath("data[].endDate").type(JsonFieldType.STRING).description("종료일"),
                    fieldWithPath("data[].refundPrice").type(JsonFieldType.NUMBER)
                        .description("환불가"),
                    fieldWithPath("data[].purchasePrice").type(JsonFieldType.NUMBER)
                        .description("구매가"),
                    fieldWithPath("data[].remainingDays").type(JsonFieldType.NUMBER)
                        .description("남은 날짜"),
                    fieldWithPath("data[].remainingTimes").type(JsonFieldType.NUMBER)
                        .description("남은 시간")
                )
            ));
    }
}
