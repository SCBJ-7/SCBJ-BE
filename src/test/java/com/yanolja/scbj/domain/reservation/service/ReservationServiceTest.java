package com.yanolja.scbj.domain.reservation.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.hotelRoom.entity.HotelRoomImage;
import com.yanolja.scbj.domain.hotelRoom.entity.Room;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.dto.response.ReservationFindResponse;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
    private ProductRepository productRepository;

    @Nested
    @DisplayName("예약 내역 상세 조회는 ")
    class Context_findReservation {

        @Test
        @DisplayName("성공시 예약내역을 반환한다.")
        void findReservation_willSuccess() {
            // given
            long memberId = 1L;
            long yanoljaId = 1L;

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

            HotelRoomImage hotelRoomImage = HotelRoomImage.builder()
                .id(1L)
                .url("naver.com")
                .build();

            Hotel hotel = Hotel.builder()
                .id(1L)
                .room(room)
                .hotelName("신라호텔")
                .hotelRoomImageList(List.of(hotelRoomImage))
                .hotelRefundPolicyList(List.of())
                .build();

            List<Reservation> reservationList = new ArrayList<>();
            List<ReservationFindResponse> reservationResList = new ArrayList<>();

            Reservation reservation1 = Reservation.builder()
                .id(1L)
                .hotel(hotel)
                .yanoljaMember(yanoljaMember)
                .purchasePrice(5000000)
                .startDate(LocalDateTime.of(2024, 2, 26, 16, 0))
                .endDate(LocalDateTime.of(2024, 2, 28, 11, 0))
                .build();

            Reservation reservation2 = Reservation.builder()
                .id(2L)
                .hotel(hotel)
                .yanoljaMember(yanoljaMember)
                .purchasePrice(4500000)
                .startDate(LocalDateTime.of(2024, 2, 10, 15, 0))
                .endDate(LocalDateTime.of(2024, 2, 11, 11, 0))
                .build();

            reservationList.add(reservation1);
            reservationList.add(reservation2);

            ReservationFindResponse toFindResponse1 = ReservationFindResponse.builder()
                .reservationId(1L)
                .hotelName(reservation1.getHotel().getHotelName())
                .purchasePrice(reservation1.getPurchasePrice())
                .remainingDays((int) Duration.between(LocalDateTime.now(),
                    reservation1.getStartDate()).toDays())
                .remainingTimes(
                    (int) Duration.between(LocalDateTime.now(),
                        reservation1.getStartDate()).toDays() * 24 + Duration.between(
                        LocalDateTime.now(),
                        reservation1.getStartDate()).toHoursPart())
                .build();

            ReservationFindResponse toFindResponse2 = ReservationFindResponse.builder()
                .reservationId(2L)
                .hotelName(reservation1.getHotel().getHotelName())
                .purchasePrice(reservation2.getPurchasePrice())
                .build();

            reservationResList.add(toFindResponse1);
            reservationResList.add(toFindResponse2);

            Product product = Product.builder()
                .id(1L)
                .reservation(reservation1)
                .build();

            product.delete(LocalDateTime.of(2024,1,26,17,0));

            given(productRepository.findByReservationId(reservation1.getId())).willReturn(
                java.util.Optional.ofNullable(product));

            given(memberRepository.findById(any(Long.TYPE))).willReturn(
                Optional.ofNullable(member));
            given(reservationRepository.findByYanoljaMemberId(any(Long.TYPE))).willReturn(
                reservationList);

            // when
            List<ReservationFindResponse> reservationFindResponse = reservationService.getReservation(
                memberId);

            // then
            Assertions.assertThat(reservationFindResponse).isNotNull();
            Assertions.assertThat(reservationFindResponse.size()).isEqualTo(2);
            Assertions.assertThat(reservationFindResponse.get(1).purchasePrice())
                .isEqualTo(4500000);
            Assertions.assertThat(reservationFindResponse.get(1).hotelName())
                .isEqualTo("신라호텔");
            Assertions.assertThat(reservationFindResponse.get(1).refundPrice()).isEqualTo(4500000);
        }
    }
}