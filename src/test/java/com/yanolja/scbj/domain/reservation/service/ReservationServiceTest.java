package com.yanolja.scbj.domain.reservation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.hotelRoom.repository.RefundPolicyRepository;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Transactional
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RefundPolicyRepository refundPolicyRepository;

    @Mock
    private ReservationDtoConverter reservationDtoConverter;


    @Nested
    @DisplayName("예약 내역 상세 조회는 ")
    class Context_findReservation {

        @Test
        @DisplayName("성공시 예약내역을 반환한다.")
        void findReservation_willSuccess() {
            // given
            long memberId = 1L;
            long yanoljaId = 1L;
            long reservationId = 1L;

            YanoljaMember yanoljaMember = YanoljaMember.builder().id(yanoljaId)
                .email("yang980329@naver.com").build();

            Member member = Member.builder().id(memberId).yanoljaMember(yanoljaMember)
                .email("yang980329@naver.com").password("yang8126042").name("양유림")
                .phone("010-3996-6042").build();

            RoomTheme roomTheme = RoomTheme.builder()
                .id(1L)
                .build();

            Room room = Room.builder()
                .checkIn(LocalTime.now())
                .checkOut(LocalTime.now())
                .roomTheme(roomTheme)
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .build();

            Reservation reservation = Reservation.builder().id(reservationId)
                .yanoljaMember(yanoljaMember).hotel(hotel).purchasePrice(5000000).startDate(
                    LocalDate.now()).endDate(LocalDate.now()).build();

            RefundPolicy refundPolicy = RefundPolicy.builder().hotel(hotel)
                .baseDate(LocalDate.now()).percent(30).build();

            ReservationFindResponse toFindResponse = ReservationFindResponse.builder()
                .hotelName(reservation.getHotel().getHotelName())
                .purchasePrice(5000000)
                .build();

            given(memberRepository.findById(any(Long.TYPE))).willReturn(
                Optional.ofNullable(member));
            given(reservationRepository.findByYanoljaMemberId(any(Long.TYPE))).willReturn(
                Optional.ofNullable(reservation));
            given(refundPolicyRepository.findByHotelId(any(Long.TYPE))).willReturn(
                Optional.ofNullable(refundPolicy));
            given(reservationDtoConverter.toFindResponse(any(), any())).willReturn(toFindResponse);

            // when
            ReservationFindResponse reservationFindResponse = reservationService.findReservation(
                memberId);

            // then
            Assertions.assertThat(reservationFindResponse).isNotNull();
            Assertions.assertThat(reservationFindResponse.getPurchasePrice()).isEqualTo(5000000);
        }
    }


}