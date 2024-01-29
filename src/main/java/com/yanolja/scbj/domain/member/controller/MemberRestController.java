package com.yanolja.scbj.domain.member.controller;

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
import com.yanolja.scbj.domain.member.service.MailService;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.member.validation.ValidationSequence;
import com.yanolja.scbj.global.common.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("v1/members")
public class MemberRestController {

    private final MemberService memberService;
    private final MailService mailService;

    public MemberRestController(MemberService memberService, MailService mailService) {
        this.memberService = memberService;
        this.mailService = mailService;
    }

    @PostMapping("/signup")
    public ResponseDTO<MemberResponse> signUp(
        @Validated(ValidationSequence.class) @RequestBody MemberSignUpRequest memberSignUpRequest) {
   
        return ResponseDTO.res(memberService.signUp(memberSignUpRequest), "회원가입에 성공했습니다.");
    }

    @PostMapping("/signin")
    public ResponseDTO<MemberSignInResponse> signIn(
        @Validated(ValidationSequence.class) @RequestBody MemberSignInRequest memberSignInRequest) {
     
        return ResponseDTO.res(memberService.signIn(memberSignInRequest), "로그인에 성공했습니다.");
    }

    @PostMapping("/logout")
    public ResponseDTO<String> logout(
        @Validated(ValidationSequence.class) @RequestBody RefreshRequest refreshRequest) {
        
        memberService.logout(refreshRequest);
        
        return ResponseDTO.res("로그아웃에 성공했습니다.");
    }

    @PatchMapping("/password")
    public ResponseDTO<String> updateMemberPassword(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdatePasswordRequest memberUpdatePasswordRequest) {

        memberService.updateMemberPassword(memberUpdatePasswordRequest);

        return ResponseDTO.res("비밀번호 변경에 성공했습니다.");
    }

    @PatchMapping("/account")
    public ResponseDTO<String> updateMemberAccount(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdateAccountRequest memberUpdateAccountRequest) {

        memberService.updateMemberAccount(memberUpdateAccountRequest);

        return ResponseDTO.res("계좌번호 등록/수정에 성공했습니다.");
    }

    @PatchMapping("/name")
    public ResponseDTO<String> updateMemberName(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdateNameRequest memberUpdateNameRequest) {

        memberService.updateMemberName(memberUpdateNameRequest.name());

        return ResponseDTO.res("이름 변경에 성공했습니다.");
    }

    @PostMapping("/email")
    public ResponseDTO<String> certifyEmail(
        @Validated(ValidationSequence.class) @RequestBody MemberEmailRequest memberEmailRequest) {

        return ResponseDTO.res(mailService.certifyEmail(memberEmailRequest.email()),
                "이메일 인증번호 발급에 성공했습니다.");
    }

    @PostMapping("/yanolja")
    public ResponseDTO<String> linkUpYanolja(
        @Validated(ValidationSequence.class) @RequestBody MemberEmailRequest memberEmailRequest) {

        memberService.linkUpYanolja(memberEmailRequest.email());

        return ResponseDTO.res("야놀자 계정 연동에 성공했습니다.");
    }

    @PatchMapping("/phone")
    public ResponseDTO<String> updateMemberPhone(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdatePhoneRequest memberUpdatePhoneRequest) {

        memberService.updateMemberPhone(memberUpdatePhoneRequest.phone());

        return ResponseDTO.res("핸드폰 번호 변경에 성공했습니다.");
    }

    @GetMapping
    public ResponseDTO<MemberResponse> getMemberInfo() {

        return ResponseDTO.res(memberService.getMemberInfo(), "회원정보 조회에 성공했습니다.");
    }


}
