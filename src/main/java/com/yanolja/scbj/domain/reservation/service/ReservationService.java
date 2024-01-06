package com.yanolja.scbj.domain.reservation.service;

import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.exception.RefundNotFoundException;
import com.yanolja.scbj.domain.hotelRoom.repository.RefundPolicyRepository;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.exception.ReservationNotFoundException;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final RefundPolicyRepository refundPolicyRepository;
    private final ReservationDtoConverter reservationDtoConverter;

    @Transactional
    public ReservationFindResponse findReservation(Long memberId) {

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));

        YanoljaMember yanoljaMember = member.getYanoljaMember();

        Reservation reservation = reservationRepository.findByYanoljaMemberId(yanoljaMember.getId())
            .orElseThrow(() -> new ReservationNotFoundException(ErrorCode.RESERVATION_NOT_FOUND));

        RefundPolicy refundPolicy = refundPolicyRepository.findByHotelId(
                reservation.getHotel().getId())
            .orElseThrow(() -> new RefundNotFoundException(ErrorCode.REFUND_NOT_FOUND));

        return reservationDtoConverter.toFindResponse(reservation, refundPolicy);
    }
}
