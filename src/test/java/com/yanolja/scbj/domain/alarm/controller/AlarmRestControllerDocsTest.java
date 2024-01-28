package com.yanolja.scbj.domain.alarm.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.alarm.dto.AlarmHasNonReadResponse;
import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.alarm.util.AlarmMapper;
import com.yanolja.scbj.global.helper.TestConstants;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;

class AlarmRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    private AlarmService alarmService;

    @Override
    public Object initController() {
        return new AlarmRestController(alarmService);
    }

    @Nested
    @DisplayName("알림 관련 API 사용시")
    class AlarmDocsRestControllerTest {
        AlarmResponse alarmResponse = AlarmResponse.builder()
            .title("알림 TEST 제목입니다.")
            .content("알림 TEST 내용입니다.")
            .date(LocalDateTime.now())
            .isRead(false)
            .build();

        @Test
        @DisplayName("알림을 조회할 때")
        void getAlarms() throws Exception {
            // given
            List<AlarmResponse> alarmResponses = new ArrayList<>();
            alarmResponses.add(alarmResponse);
            given(alarmService.getAlarms()).willReturn(alarmResponses);

            // when & then
            mockMvc.perform(get("/v1/alarms")
                    .header("Authorization", TestConstants.ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data[].id").type(Number.class).description("사용자 식별자"),
                        fieldWithPath("data[].title").type(String.class).description("알림 제목"),
                        fieldWithPath("data[].content").type(String.class).description("알림 내용"),
                        fieldWithPath("data[].date").type(LocalDateTime.class).description("알림 발생일"),
                        fieldWithPath("data[].isRead").type(Boolean.class).description("알림 읽음 여부")
                    )
                ));
        }

        @Test
        @DisplayName("알림 읽음 여부를 조회할 때")
        void hasNonReadAlarm() throws Exception {
            // given
            AlarmHasNonReadResponse alarmHasNonReadResponse = AlarmMapper.toAlarmHasNonReadResponse(true);
            given(alarmService.hasNonReadAlarm()).willReturn(alarmHasNonReadResponse);

            // when & then
            mockMvc.perform(get("/v1/alarms/status")
                    .header("Authorization", TestConstants.ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data.hasNonReadAlarm").type(Boolean.class).description("안 읽음 알림 있는지 여부")
                    )
                ));
 }
    }
}