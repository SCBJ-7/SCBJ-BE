package com.yanolja.scbj.domain.member.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.yanolja.scbj.domain.member.dto.request.MemberSignInRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.MemberResponse;
import com.yanolja.scbj.domain.member.dto.response.MemberSignInResponse;
import com.yanolja.scbj.domain.member.entity.Authority;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.global.helper.TestConstants;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.member.repository.YanoljaMemberRepository;
import com.yanolja.scbj.domain.member.util.MemberMapper;
import com.yanolja.scbj.global.config.fcm.FCMService;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private SecurityUtil securityUtil;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private MemberRepository memberRepository;

    @Mock
    private FCMService fcmService;

    @Mock
    private YanoljaMemberRepository yanoljaMemberRepository;
    @InjectMocks
    private MemberService memberService;

    @Nested
    @DisplayName("유저 관련 서비스 사용 시")
    class SuccessTests {

        private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        private String testRawPassword = "test1234@";
        private Member testMember = Member.builder()
            .id(1L)
            .email("test@gmail.com")
            .authority(Authority.ROLE_USER)
            .password(bCryptPasswordEncoder.encode(testRawPassword))
            .name("test")
            .phone("010-1234-5678")
            .yanoljaMember(YanoljaMember.builder()
                .email("test@gmail.com")
                .build())
            .build();

        @Test
        @DisplayName("회원가입할 때")
        void signUp() {
            //given
            MemberSignUpRequest memberSignUpRequest = MemberSignUpRequest.builder()
                .email(testMember.getEmail())
                .name(testMember.getName())
                .password(testRawPassword)
                .phone(testMember.getPhone())
                .build();

            MemberResponse expectedMemberResponse = MemberResponse.builder()
                .email(testMember.getEmail())
                .name(testMember.getName())
                .phone(testMember.getPhone())
                .id(testMember.getId())
                .linkedToYanolja(true)
                .build();

            given(memberRepository.existsByEmail(any(String.class))).willReturn(false);
            given(memberRepository.save(any(Member.class))).willReturn(testMember);
            //when & then
            assertThat(expectedMemberResponse).usingRecursiveComparison()
                .isEqualTo(memberService.signUp(memberSignUpRequest));

        }

        @Test
        @DisplayName("로그인할 때")
        void signIn() {
            //given
            MemberSignInRequest memberSignInRequest = MemberSignInRequest.builder()
                .email(testMember.getEmail())
                .password(testRawPassword)
                .build();

            MemberSignInResponse memberSignInResponse = MemberSignInResponse.builder()
                .memberResponse(MemberMapper.toMemberResponse(testMember))
                .tokenResponse(MemberMapper.toTokenResponse("", ""))
                .build();
            given(memberRepository.findByEmail(any())).willReturn(Optional.of(testMember));
            given(passwordEncoder.matches(any(), any())).willReturn(true);
            given(jwtUtil.generateToken(any())).willReturn("");
            given(jwtUtil.generateRefreshToken(any())).willReturn("");
            //when & then
            assertThat(memberSignInResponse).usingRecursiveComparison()
                .isEqualTo(memberService.signIn(memberSignInRequest));

        }

        @Test
        @DisplayName("로그아웃할 때")
        void logout() {
            //given
            RefreshRequest refreshRequest = RefreshRequest.builder()
                .accessToken(TestConstants.GRANT_TYPE.getValue())
                .refreshToken(TestConstants.REFRESH_PREFIX.getValue()).build();

            // when
            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(memberRepository.findById(1L)).willReturn(Optional.of(testMember));
            given(jwtUtil.extractUsername(any())).willReturn("1");
            given(jwtUtil.isRefreshTokenValid(any(), any())).willReturn(true);
            memberService.logout(refreshRequest);
            // then
            verify(jwtUtil, times(1)).setBlackList(refreshRequest.getAccessToken().substring(7),
                refreshRequest.getRefreshToken());
        }

        @Test
        @DisplayName("비밀번호 수정 시")
        void updateMemberPassword() {
            //given
            String changedPassword = "test1234!";
            String encodedPassword = passwordEncoder.encode("test1234!");
            MemberUpdatePasswordRequest memberUpdatePasswordRequest = MemberUpdatePasswordRequest.builder()
                .email(testMember.getEmail())
                .password(changedPassword).build();
            given(memberRepository.findByEmail(any())).willReturn(Optional.of(testMember));
            given(passwordEncoder.encode(changedPassword)).willReturn(
                encodedPassword);
            //when
            memberService.updateMemberPassword(memberUpdatePasswordRequest);
            //then
            assertEquals(testMember.getEmail(), memberUpdatePasswordRequest.email());
            assertEquals(testMember.getPassword(), encodedPassword);
        }

        @Test
        @DisplayName("계좌번호 수정 시")
        void updateMemberAccount() {
            //given
            MemberUpdateAccountRequest memberUpdateAccountRequest = MemberUpdateAccountRequest.builder()
                .accountNumber("123456789")
                .bank("농협")
                .build();

            given(memberRepository.findById(any())).willReturn(Optional.of(testMember));
            //when
            memberService.updateMemberAccount(memberUpdateAccountRequest);
            //then
            assertEquals(testMember.getAccountNumber(), memberUpdateAccountRequest.accountNumber());
            assertEquals(testMember.getBank(), memberUpdateAccountRequest.bank());

        }

        @Test
        @DisplayName("이름 수정 시")
        void updateMemberName() {
            //given
            String nameToUpdate = "이상해씨";

            given(memberRepository.findById(any())).willReturn(Optional.of(testMember));
            //when
            memberService.updateMemberName(nameToUpdate);
            //then
            assertEquals(testMember.getName(), nameToUpdate);

        }

        @Test
        @DisplayName("야놀자 계정 연동 시")
        void linkUpYanoljaMember() {
            //given
            String yanoljaEmail = "test@gmail.com";
            YanoljaMember yanoljaMember = YanoljaMember.builder()
                .id(1L)
                .email("test@gmail.com")
                .build();
            given(memberRepository.findById(any())).willReturn(Optional.of(testMember));
            given(yanoljaMemberRepository.findByEmail(yanoljaEmail)).willReturn(
                Optional.of(yanoljaMember));
            //when
            memberService.linkUpYanolja(yanoljaEmail);
            //then
            assertEquals(testMember.getYanoljaMember(), yanoljaMember);
            verify(yanoljaMemberRepository, times(1)).findByEmail(yanoljaEmail);
        }

        @Test
        @DisplayName("핸드폰 번호 수정 시")
        void updateMemberPhone() {
            //given
            String phoneToUpdate = "010-1234-5678";

            given(memberRepository.findById(any())).willReturn(Optional.of(testMember));
            //when
            memberService.updateMemberPhone(phoneToUpdate);
            //then
            assertEquals(testMember.getPhone(), phoneToUpdate);
        }

        @Test
        @DisplayName("회원정보 조회 시")
        void getMemberInfo() {
            //given
            given(memberRepository.findById(any())).willReturn(Optional.of(testMember));
            //when
            MemberResponse resultMemberResponse = memberService.getMemberInfo();
            //then
            assertThat(MemberMapper.toMemberResponse(testMember)).usingRecursiveComparison()
                .isEqualTo(resultMemberResponse);
        }
    }
}