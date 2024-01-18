package com.yanolja.scbj.domain.alarm.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.domain.alarm.util.AlarmMapper;
import com.yanolja.scbj.global.config.SecurityConfig;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(
    controllers = AlarmRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class AlarmRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private AlarmService alarmService;

    @Nested
    @DisplayName("알림 관련 API 사용시")
    class SuccessTests {

        AlarmResponse alarmResponse = AlarmResponse.builder()
            .title("알림 TEST 제목입니다.")
            .content("알림 TEST 내용입니다.")
            .date(LocalDateTime.now())
            .build();

        @Test
        @DisplayName("알림을 조회할 때")
        void getAlarms() throws Exception {
            // given
            List<AlarmResponse> alarmResponses = new ArrayList<>();
            alarmResponses.add(alarmResponse);
            given(alarmService.getAlarms()).willReturn(alarmResponses);

            // when & then
            mockMvc.perform(get("/v1/alarms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value(alarmResponse.title()))
                .andExpect(jsonPath("$.data[0].content").value(alarmResponse.content()))
                .andExpect(jsonPath("$.data[0].date").value(alarmResponse.date()))
                .andDo(print());
        }
    }
}