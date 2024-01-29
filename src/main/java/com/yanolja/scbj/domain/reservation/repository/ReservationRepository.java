package com.yanolja.scbj.domain.reservation.repository;

import com.yanolja.scbj.domain.reservation.entity.Reservation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByIdAndYanoljaMemberId(Long reservationId, Long yanoljaMemberId);

    List<Reservation> findByYanoljaMemberId(Long yanoljaMemberId);
}
