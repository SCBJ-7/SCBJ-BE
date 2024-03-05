package com.yanolja.scbj.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.service.MemberAuthService;
import com.yanolja.scbj.global.helper.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;


class MemberAuthRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    private MemberAuthService memberAuthService;

    @Override
    public Object initController() {
        return new MemberAuthRestController(memberAuthService);
    }

    @Test
    @DisplayName("리프레쉬 토큰 재발급할 때")
    void refreshAccessToken() throws Exception {
        // given
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken(TestConstants.GRANT_TYPE.getValue())
            .refreshToken(TestConstants.REFRESH_PREFIX.getValue())
            .build();
        RefreshRequest refreshRequest = RefreshRequest.builder()
            .accessToken(TestConstants.GRANT_TYPE.getValue())
            .refreshToken(TestConstants.REFRESH_PREFIX.getValue())
            .build();
        given(memberAuthService.refreshAccessToken(any(RefreshRequest.class))).willReturn(
            tokenResponse);

        // when & then
        mockMvc.perform(post("/v1/token/refresh")
                .content(objectMapper.writeValueAsString(refreshRequest))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(restDoc.document(
                requestFields(
                    fieldWithPath("accessToken").type(String.class).description("만료된 액세스 토큰"),
                    fieldWithPath("refreshToken").type(String.class).description("리프레쉬 토큰")
                ),
                responseFields(responseCommon()).and(
                    fieldWithPath("data.accessToken").type(Number.class).description("재발급된 액세스 토큰"),
                    fieldWithPath("data.refreshToken").type(Number.class).description("리프레쉬 토큰")
                )
            ));


    }
}