package com.yanolja.scbj.global.config.fcm;

import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@ResponseStatus(HttpStatus.OK)
@RequestMapping("/v1/fcm")
public class FCMRestController {

    private final FCMService fcmService;

    @PostMapping
    public ResponseDTO<String> saveFCMToken(@RequestParam(name = "email") String email, @RequestParam(name = "token") String token) {
        fcmService.saveToken(email, token);
        return ResponseDTO.res("토큰 저장에 성공했습니다.");
    }

    @PostMapping("/alarm")
    public ResponseDTO<String> pushAlarm(@RequestParam(name = "email") String email) {
        fcmService.sendMessageTo(email, new Data("TEST용 알림", "TEST용 푸쉬 알림이 생성되었습니다.", LocalDateTime.now()));
        return ResponseDTO.res("푸쉬 알림에 성공했습니다.");
    }
}