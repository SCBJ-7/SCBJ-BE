package com.yanolja.scbj.domain.member.controller;

import com.yanolja.scbj.domain.member.dto.request.RefreshRequest;
import com.yanolja.scbj.domain.member.dto.response.TokenResponse;
import com.yanolja.scbj.domain.member.service.MemberAuthService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/token")
public class MemberAuthRestController {

    private final MemberAuthService memberAuthService;

    MemberAuthRestController(MemberAuthService memberAuthService) {
        this.memberAuthService = memberAuthService;
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(
        @RequestBody RefreshRequest refreshRequest) {
        log.info("accessToken:{}, refreshToken:{}", refreshRequest.getAccessToken(),
            refreshRequest.getRefreshToken());
        return ResponseEntity.ok().body(memberAuthService.refreshAccessToken(refreshRequest));
    }

}
