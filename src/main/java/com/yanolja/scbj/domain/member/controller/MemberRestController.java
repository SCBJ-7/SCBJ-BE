package com.yanolja.scbj.domain.member.controller;

import com.yanolja.scbj.domain.member.dto.request.MemberSignInRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.yanolja.scbj.domain.member.dto.response.MemberResponse;
import com.yanolja.scbj.domain.member.dto.response.MemberSignInResponse;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.global.common.ResponseDTO;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<MemberResponse>> signUp(
        @Valid @RequestBody MemberSignUpRequest memberSignUpRequest) {
        log.info("email:{}, password:{}, name:{}, phone:{}", memberSignUpRequest.email(),
            memberSignUpRequest.password(),
            memberSignUpRequest.name(), memberSignUpRequest.phone());
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.signUp(memberSignUpRequest),
                "성공적으로 회원가입했습니다."));
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseDTO<MemberSignInResponse>> signIn(
        @Valid @RequestBody MemberSignInRequest memberSignInRequest) {
        log.info("email:{}, password:{}", memberSignInRequest.email(),
            memberSignInRequest.password());
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.signIn(memberSignInRequest),
                "성공적으로 로그인했습니다."));
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseDTO<String>> updateMemberPassword(@Valid @RequestBody
    MemberUpdatePasswordRequest memberUpdatePasswordRequest) {
        log.info("password:{}", memberUpdatePasswordRequest.password());
        memberService.updateMemberPassword(memberUpdatePasswordRequest);

        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 비밀번호를 변경했습니다."));
    }

    @PatchMapping("/account")
    public ResponseEntity<ResponseDTO<String>> updateMemberAccount(@Valid @RequestBody
    MemberUpdateAccountRequest memberUpdateAccountRequest) {
        log.info("accountNumber:{}, bank:{}", memberUpdateAccountRequest.accountNumber(),
            memberUpdateAccountRequest.bank());
        memberService.updateMemberAccount(memberUpdateAccountRequest);

        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 계좌번호를 등록/수정했습니다."));
    }


}
