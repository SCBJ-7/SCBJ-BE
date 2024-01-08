package com.yanolja.scbj.domain.member.controller;

import com.yanolja.scbj.domain.member.dto.request.MemberSignInRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberSignUpRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdateAccountRequest;
import com.yanolja.scbj.domain.member.dto.request.MemberUpdatePasswordRequest;
import com.yanolja.scbj.domain.member.dto.response.MemberResponse;
import com.yanolja.scbj.domain.member.dto.response.MemberSignInResponse;
import com.yanolja.scbj.domain.member.service.MemberService;
import com.yanolja.scbj.domain.member.validation.Phone;
import com.yanolja.scbj.global.common.ResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    MemberRestController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDTO<MemberResponse>> signUp(
        @Valid @RequestBody MemberSignUpRequest memberSignUpRequest) {
        log.info("email:{}, password:{}, name:{}, phone:{}", memberSignUpRequest.email(),
            memberSignUpRequest.password(), memberSignUpRequest.name(),
            memberSignUpRequest.phone());
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.signUp(memberSignUpRequest), "성공적으로 회원가입했습니다."));
    }

    @PostMapping("/signin")
    public ResponseEntity<ResponseDTO<MemberSignInResponse>> signIn(
        @Valid @RequestBody MemberSignInRequest memberSignInRequest) {
        log.info("email:{}, password:{}", memberSignInRequest.email(),
            memberSignInRequest.password());
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.signIn(memberSignInRequest), "성공적으로 로그인했습니다."));
    }

    @PatchMapping("/password")
    public ResponseEntity<ResponseDTO<String>> updateMemberPassword(
        @Valid @RequestBody MemberUpdatePasswordRequest memberUpdatePasswordRequest) {
        log.info("password:{}", memberUpdatePasswordRequest.password());
        memberService.updateMemberPassword(memberUpdatePasswordRequest);

        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 비밀번호를 변경했습니다."));
    }

    @PatchMapping("/account")
    public ResponseEntity<ResponseDTO<String>> updateMemberAccount(
        @Valid @RequestBody MemberUpdateAccountRequest memberUpdateAccountRequest) {
        log.info("accountNumber:{}, bank:{}", memberUpdateAccountRequest.accountNumber(),
            memberUpdateAccountRequest.bank());
        memberService.updateMemberAccount(memberUpdateAccountRequest);

        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 계좌번호를 등록/수정했습니다."));
    }

    @PatchMapping("/name")
    public ResponseEntity<ResponseDTO<String>> updateMemberName(
        @Size(min = 1, max = 20, message = "이름의 길이는 1 ~ 20 이어야 합니다.")
        @Pattern(regexp = "[^0-9]*", message = "이름에 숫자는 입력할 수 없습니다.")
        @RequestBody String nameToUpdate) {
        memberService.updateMemberName(nameToUpdate);

        return ResponseEntity.ok().body(ResponseDTO.res("이름을 성공적으로 변경했습니다."));
    }

    @PatchMapping("/phone")
    public ResponseEntity<ResponseDTO<String>> updateMemberPhone(
        @Phone
        @RequestBody String phoneToUpdate) {
        memberService.updateMemberPhone(phoneToUpdate);
        return ResponseEntity.ok().body(ResponseDTO.res("성공적으로 핸드폰 번호를 변경했습니다."));
    }

    @GetMapping
    public ResponseEntity<ResponseDTO<MemberResponse>> getMemberInfo() {
        return ResponseEntity.ok()
            .body(ResponseDTO.res(memberService.getMemberInfo(), "성공적으로 회원정보를 조회했습니다."));
    }

}
