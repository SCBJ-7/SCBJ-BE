package com.yanolja.scbj.domain.alarm.controller;

import com.yanolja.scbj.domain.alarm.dto.AlarmResponse;
import com.yanolja.scbj.domain.alarm.service.AlarmService;
import com.yanolja.scbj.global.common.ResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/alarms")
@RequiredArgsConstructor
public class AlarmRestController {

    private final AlarmService alarmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<List<AlarmResponse>> getAlarms() {
        return ResponseDTO.res(alarmService.getAlarms(), "알람 조회에 성공했습니다.");
    }

}
