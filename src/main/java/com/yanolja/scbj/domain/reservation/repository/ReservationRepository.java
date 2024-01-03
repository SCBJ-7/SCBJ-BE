package com.yanolja.scbj.domain.reservation.repository;

import com.yanolja.scbj.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

}
