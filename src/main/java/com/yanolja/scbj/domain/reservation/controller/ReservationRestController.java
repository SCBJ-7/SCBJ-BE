package com.yanolja.scbj.domain.reservation.controller;

import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.service.ReservationService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/reservations")
public class ReservationRestController {

    private final ReservationService reservationService;
    private final SecurityUtil securityUtil;

    @GetMapping
    public ResponseEntity<ResponseDTO<ReservationFindResponse>> findReservation() {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ResponseDTO.res(
                reservationService.findReservation(securityUtil.getCurrentMemberId()),
                "예약 내역 조회에 성공했습니다."));
    }
}
