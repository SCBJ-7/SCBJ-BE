package com.yanolja.scbj.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.service.MemberAuthService;
import com.yanolja.scbj.global.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(
    controllers = MemberAuthRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class MemberAuthRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberAuthService memberAuthService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void refreshAccessToken() throws Exception {
        // given
        TokenResponse tokenResponse = TokenResponse.builder().accessToken("").refreshToken("")
            .build();
        given(memberAuthService.refreshAccessToken(any(RefreshRequest.class))).willReturn(tokenResponse);

        // when & then
        mockMvc.perform(post("/v1/token/refresh")
            .content(objectMapper.writeValueAsString(tokenResponse))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print());


    }
}