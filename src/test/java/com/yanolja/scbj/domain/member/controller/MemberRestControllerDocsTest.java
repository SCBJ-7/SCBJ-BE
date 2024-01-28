package com.yanolja.scbj.domain.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.docs.RestDocsSupport;
import com.yanolja.scbj.domain.member.dto.request.MemberEmailRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberSignInRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateNameRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdatePhoneRequest;
import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.MemberResponse;
import com.yanolja.scbj.domain.member.dto.response.MemberSignInResponse;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.global.helper.TestConstants;
import com.yanolja.scbj.domain.member.service.MailService;
import com.yanolja.scbj.domain.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;


@ExtendWith(MockitoExtension.class)
class MemberRestControllerDocsTest extends RestDocsSupport {

    @MockBean
    private MemberService memberService;

    @MockBean
    private MailService mailService;

    @Override
    public Object initController() {
        return new MemberRestController(memberService, mailService);
    }


    @Nested
    @DisplayName("멤버 관련 API 사용시")
    class MemberDocsRestControllerTest {

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
                .andDo(restDoc.document(
                    requestFields(
                        fieldWithPath("email").type(String.class).description("사용자 이메일"),
                        fieldWithPath("name").type(String.class).description("사용자 이름"),
                        fieldWithPath("password").type(String.class).description("사용자 비밀번호"),
                        fieldWithPath("phone").type(String.class).description("사용자 핸드폰 번호"),
                        fieldWithPath("privacyPolicy").type(Boolean.class)
                            .description("개인정보 처리 방침 여부"),
                        fieldWithPath("termOfUse").type(Boolean.class).description("이용 약관 여부")
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data.id").type(Number.class).description("사용자 식별자"),
                        fieldWithPath("data.email").type(Number.class).description("사용자 이메일"),
                        fieldWithPath("data.name").type(Number.class).description("사용자 이름"),
                        fieldWithPath("data.phone").type(Number.class).description("사용자 핸드폰 번호"),
                        fieldWithPath("data.accountNumber").type(Number.class)
                            .description("사용자 계좌 번호"),
                        fieldWithPath("data.bank").type(Number.class).description("사용자 계좌"),
                        fieldWithPath("data.linkedToYanolja").type(Number.class)
                            .description("야놀자 연동 여부")
                    )
                ));

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
                .andDo(restDoc.document(
                    requestFields(
                        fieldWithPath("email").type(String.class).description("사용자 이메일"),
                        fieldWithPath("password").type(String.class).description("사용자 비밀번호"),
                        fieldWithPath("fcmToken").type(String.class).description("사용자 기기 식별자 토큰")
                            .optional()
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data.memberResponse.id").type(Number.class)
                            .description("사용자 식별자"),
                        fieldWithPath("data.memberResponse.email").type(Number.class)
                            .description("사용자 이메일"),
                        fieldWithPath("data.memberResponse.name").type(Number.class)
                            .description("사용자 이름"),
                        fieldWithPath("data.memberResponse.phone").type(Number.class)
                            .description("사용자 핸드폰 번호"),
                        fieldWithPath("data.memberResponse.accountNumber").type(Number.class)
                            .description("사용자 계좌 번호"),
                        fieldWithPath("data.memberResponse.bank").type(Number.class)
                            .description("사용자 계좌"),
                        fieldWithPath("data.memberResponse.linkedToYanolja").type(Number.class)
                            .description("야놀자 연동 여부"),
                        fieldWithPath("data.tokenResponse.accessToken").type(String.class)
                            .description("액세스 토큰"),
                        fieldWithPath("data.tokenResponse.refreshToken").type(String.class)
                            .description("리프레쉬 토큰")
                    )
                ));

        }

        @Test
        @DisplayName("로그아웃 할 때")
        void logout() throws Exception {
            //given
            RefreshRequest refreshRequest = RefreshRequest.builder()
                .accessToken(TestConstants.GRANT_TYPE.getValue())
                .refreshToken(TestConstants.REFRESH_PREFIX.getValue())
                .build();
            //when & then
            mockMvc.perform(post("/v1/members/logout")
                    .header("Authorization",  TestConstants.ACCESS_TOKEN)
                    .content(objectMapper.writeValueAsString(refreshRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data").type(Object.class).description("응답 데이터")
                    )
                ));
        }

        @Test
        @DisplayName("비밀번호 수정 시")
        void updateMemberPassword() throws Exception {
            //given
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = MemberUpdatePasswordRequest
                .builder().password(testPassword).build();
            //when & then
            mockMvc.perform(patch("/v1/members/password")
                    .header("Authorization",  TestConstants.ACCESS_TOKEN)
                    .content(objectMapper.writeValueAsString(memberUpdatePasswordRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    requestFields(
                        fieldWithPath("password").type(String.class).description("사용자 비밀번호"),
                        fieldWithPath("email").type(String.class).description("사용자 이메일")
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data").type(Object.class).description("응답 데이터")
                    )
                ));
        }

        @Test
        @DisplayName("계좌번호 수정 시")
        void updateMemberAccount() throws Exception {
            //given
            MemberUpdateAccountRequest memberUpdateAccountRequest = MemberUpdateAccountRequest
                .builder().accountNumber("1233456783").bank("농협").build();
            //when & then
            mockMvc.perform(patch("/v1/members/account")
                    .header("Authorization",  TestConstants.ACCESS_TOKEN)
                    .content(objectMapper.writeValueAsString(memberUpdateAccountRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    requestFields(
                        fieldWithPath("accountNumber").type(String.class).description("사용자 계좌 번호"),
                        fieldWithPath("bank").type(String.class).description("사용자 계좌")
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data").type(Object.class).description("응답 데이터")
                    )
                ));
        }

        @Test
        @DisplayName("이름 수정 시")
        void updateMemberName() throws Exception {
            //given
            MemberUpdateNameRequest memberUpdateNameRequest = MemberUpdateNameRequest.builder()
                .name(memberResponse.getName()).build();
            //when & then
            mockMvc.perform(patch("/v1/members/name")
                    .header("Authorization",  TestConstants.ACCESS_TOKEN)
                    .content(objectMapper.writeValueAsString(memberUpdateNameRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    requestFields(
                        fieldWithPath("name").type(String.class).description("사용자 이름")
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data").type(Object.class).description("응답 데이터")
                    )
                ));
        }

        @Test
        @DisplayName("이메일 인증 시")
        void certifyEmail() throws Exception {
            //given
            MemberEmailRequest memberEmailRequest = MemberEmailRequest.builder()
                .email(memberResponse.getEmail()).build();
            given(mailService.certifyEmail(memberEmailRequest.email())).willReturn("123456");
            //when & then
            mockMvc.perform(post("/v1/members/email")
                    .content(objectMapper.writeValueAsString(memberEmailRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    requestFields(
                        fieldWithPath("email").type(String.class).description("사용자 이메일")
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data").type(String.class)
                            .description("이메일 인증 번호")
                    )
                ));
        }

        @Test
        @DisplayName("야놀자 계정 연동 시")
        void linkUpYanolja() throws Exception {
            //given
            MemberEmailRequest memberEmailRequest = MemberEmailRequest.builder()
                .email(memberResponse.getEmail()).build();
            //when & then
            mockMvc.perform(post("/v1/members/yanolja")
                    .header("Authorization",  TestConstants.ACCESS_TOKEN)
                    .content(objectMapper.writeValueAsString(memberEmailRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    requestFields(
                        fieldWithPath("email").type(String.class).description("사용자 이메일")
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data").type(Object.class)
                            .description("응답 데이터")
                    )
                ));
        }

        @Test
        @DisplayName("핸드폰 수정 시")
        void updateMemberPhone() throws Exception {
            //given
            MemberUpdatePhoneRequest memberUpdatePhoneRequest = MemberUpdatePhoneRequest.builder()
                .phone(memberResponse.getPhone())
                .build();
            //when & then
            mockMvc.perform(patch("/v1/members/phone")
                    .header("Authorization",  TestConstants.ACCESS_TOKEN)
                    .content(objectMapper.writeValueAsString(memberUpdatePhoneRequest))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                    jwtHeader(),
                    requestFields(
                        fieldWithPath("phone").type(String.class).description("사용자 핸드폰 번호")
                    ),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data").type(Object.class)
                            .description("응답 데이터")
                    )
                ));
        }

        @Test
        @DisplayName("회원정보 조회 시")
        void getMemberInfo() throws Exception {
            //given
            given(memberService.getMemberInfo()).willReturn(memberResponse);
            //when & then
            mockMvc.perform(get("/v1/members")
                    .header("Authorization",  TestConstants.ACCESS_TOKEN))
                .andExpect(status().isOk())
                .andDo(restDoc.document(
                        jwtHeader(),
                    responseFields(responseCommon()).and(
                        fieldWithPath("data.id").type(Number.class).description("사용자 식별자"),
                        fieldWithPath("data.email").type(Number.class).description("사용자 이메일"),
                        fieldWithPath("data.name").type(Number.class).description("사용자 이름"),
                        fieldWithPath("data.phone").type(Number.class).description("사용자 핸드폰 번호"),
                        fieldWithPath("data.accountNumber").type(Number.class)
                            .description("사용자 계좌 번호"),
                        fieldWithPath("data.bank").type(Number.class).description("사용자 계좌"),
                        fieldWithPath("data.linkedToYanolja").type(Number.class)
                            .description("야놀자 연동 여부")
                    )
                ));
        }


    }

}