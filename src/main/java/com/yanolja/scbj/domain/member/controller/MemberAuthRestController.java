package com.yanolja.scbj.domain.member.controller;

import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.service.MemberAuthService;
import com.yanolja.scbj.global.common.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/v1/token")
public class MemberAuthRestController {

    private final MemberAuthService memberAuthService;

    public MemberAuthRestController(MemberAuthService memberAuthService) {
        this.memberAuthService = memberAuthService;
    }

    @PostMapping("/refresh")
    public ResponseDTO<TokenResponse> refreshAccessToken(
        @Valid @RequestBody RefreshRequest refreshRequest) {

        return ResponseDTO.res(memberAuthService.refreshAccessToken(refreshRequest),"토큰 재발급에 성공했습니다.");
    }

}
