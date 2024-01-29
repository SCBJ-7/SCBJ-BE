package com.yanolja.scbj.domain.reservation.controller;

import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.service.ReservationService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/reservations")
public class ReservationRestController {

    private final ReservationService reservationService;
    private final SecurityUtil securityUtil;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<List<ReservationFindResponse>> getReservation() {
        return ResponseDTO.res(
            reservationService.getReservation(securityUtil.getCurrentMemberId()),
            "예약 내역 조회에 성공했습니다.");
    }
}
