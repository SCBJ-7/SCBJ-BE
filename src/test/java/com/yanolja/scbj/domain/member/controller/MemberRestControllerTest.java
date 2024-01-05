package com.yanolja.scbj.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.member.dto.request.MemberSignInRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.yanolja.scbj.domain.member.dto.response.MemberResponse;
import com.yanolja.scbj.domain.member.dto.response.MemberSignInResponse;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.global.config.SecurityConfig;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(
    controllers = MemberRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MemberService memberService;

    @Autowired
    private ObjectMapper objectMapper;


    @Nested
    @DisplayName("멤버 관련 API 사용시")
    class SuccessTests {

        private String testPassword = "test1234@";
        private MemberResponse memberResponse = MemberResponse.builder()
            .id(1L)
            .email("test@gmail.com")
            .phone("010-1234-5678")
            .name("test")
            .build();

        private TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken("")
            .refreshToken("")
            .build();
        private MemberSignInResponse memberSignInResponse = MemberSignInResponse.builder()
            .memberResponse(memberResponse)
            .tokenResponse(tokenResponse)
            .build();

        @Test
        @DisplayName("회원가입할 때")
        void signUp() throws Exception {
            //given
            MemberSignUpRequest memberSignUpRequest = MemberSignUpRequest.builder()
                .email(memberResponse.getEmail()).password(testPassword)
                .phone(memberResponse.getPhone())
                .name(memberResponse.getName()).build();

            given(memberService.signUp(any(MemberSignUpRequest.class))).willReturn(
                memberResponse);
            //when & then
            mockMvc.perform(post("/v1/members/signup")
                    .content(objectMapper.writeValueAsString(memberSignUpRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        }

        @Test
        @DisplayName("로그인할 때")
        void signIn() throws Exception {
            //given
            MemberSignInRequest memberSignInRequest = MemberSignInRequest.builder()
                .email(memberResponse.getEmail()).password(testPassword).build();
            given(memberService.signIn(any(MemberSignInRequest.class))).willReturn(
                memberSignInResponse);
            //when & then
            mockMvc.perform(post("/v1/members/signin")
                    .content(objectMapper.writeValueAsString(memberSignInRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("비밀번호 수정 시")
        void updateMemberPassword() throws Exception {
            //given
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = MemberUpdatePasswordRequest
                .builder().password(testPassword).build();
            //when & then
            mockMvc.perform(patch("/v1/members/password")
                    .content(objectMapper.writeValueAsString(memberUpdatePasswordRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        }

        @Test
        @DisplayName("계좌번호 수정 시")
        void updateMemberAccount() throws Exception {
            //given
            MemberUpdateAccountRequest memberUpdateAccountRequest = MemberUpdateAccountRequest
                .builder().accountNumber("123-345-6783").bank("농협").build();
            //when & then
            mockMvc.perform(patch("/v1/members/account")
                    .content(objectMapper.writeValueAsString(memberUpdateAccountRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
        }
    }
}