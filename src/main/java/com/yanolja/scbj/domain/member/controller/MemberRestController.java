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
import com.yanolja.scbj.domain.member.validation.ValidationGroups;
import com.yanolja.scbj.domain.member.validation.ValidationSequence;
import com.yanolja.scbj.global.common.ResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("v1/members")
public class MemberRestController {

    private final MemberService memberService;

    private final MailService mailService;

    MemberRestController(MemberService memberService, MailService mailService) {
        this.memberService = memberService;
        this.mailService = mailService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<MemberResponse>> signUp(
        @Validated(ValidationSequence.class) @RequestBody MemberSignUpRequest memberSignUpRequest) {
        log.info("email:{}, password:{}, name:{}, phone:{}", memberSignUpRequest.email(),
            memberSignUpRequest.password(), memberSignUpRequest.name(),
            memberSignUpRequest.phone());
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.signUp(memberSignUpRequest), "성공적으로 회원가입했습니다."));
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseDTO<MemberSignInResponse>> signIn(
        @Validated(ValidationSequence.class) @RequestBody MemberSignInRequest memberSignInRequest) {
        log.info("email:{}, password:{}", memberSignInRequest.email(),
            memberSignInRequest.password());
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.signIn(memberSignInRequest), "성공적으로 로그인했습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseDTO<String>> logout(
        @Validated(ValidationSequence.class) @RequestBody RefreshRequest refreshRequest) {
        memberService.logout(refreshRequest);
        return ResponseEntity.ok()
            .body(ResponseDTO.res("성공적으로 로그아웃했습니다."));
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseDTO<String>> updateMemberPassword(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdatePasswordRequest memberUpdatePasswordRequest) {
        log.info("password:{}", memberUpdatePasswordRequest.password());
        memberService.updateMemberPassword(memberUpdatePasswordRequest);

        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 비밀번호를 변경했습니다."));
    }

    @PatchMapping("/account")
    public ResponseEntity<ResponseDTO<String>> updateMemberAccount(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdateAccountRequest memberUpdateAccountRequest) {
        log.info("accountNumber:{}, bank:{}", memberUpdateAccountRequest.accountNumber(),
            memberUpdateAccountRequest.bank());
        memberService.updateMemberAccount(memberUpdateAccountRequest);

        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 계좌번호를 등록/수정했습니다."));
    }

    @PatchMapping("/name")
    public ResponseEntity<ResponseDTO<String>> updateMemberName(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdateNameRequest memberUpdateNameRequest) {
        memberService.updateMemberName(memberUpdateNameRequest.name());

        return ResponseEntity.ok().body(ResponseDTO.res("이름을 성공적으로 변경했습니다."));
    }

    @PostMapping("/email")
    public ResponseEntity<ResponseDTO<String>> certifyEmail(
        @Validated(ValidationSequence.class) @RequestBody MemberEmailRequest memberEmailRequest) {
        return ResponseEntity.ok()
            .body(ResponseDTO.res(mailService.certifyEmail(memberEmailRequest.email()),
                "이메일 인증번호를 성공적으로 발급했습니다."));
    }

    @PostMapping("/yanolja")
    public ResponseEntity<ResponseDTO<String>> linkUpYanolja(
        @Validated(ValidationSequence.class) @RequestBody MemberEmailRequest memberEmailRequest
    ) {
        memberService.linkUpYanolja(memberEmailRequest.email());
        return ResponseEntity.ok().body(ResponseDTO.res("야놀자 계정과 성공적으로 연동했습니다."));
    }

    @PatchMapping("/phone")
    public ResponseEntity<ResponseDTO<String>> updateMemberPhone(
        @Validated(ValidationSequence.class) @RequestBody MemberUpdatePhoneRequest memberUpdatePhoneRequest) {
        memberService.updateMemberPhone(memberUpdatePhoneRequest.phone());
        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 핸드폰 번호를 변경했습니다."));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<MemberResponse>> getMemberInfo() {
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.getMemberInfo(), "성공적으로 회원정보를 조회했습니다."));
    }

}
