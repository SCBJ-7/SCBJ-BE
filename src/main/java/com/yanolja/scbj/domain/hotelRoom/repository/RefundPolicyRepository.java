package com.yanolja.scbj.domain.hotelRoom.repository;

import com.yanolja.scbj.domain.hotelRoom.entity.RefundPolicy;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefundPolicyRepository extends JpaRepository<RefundPolicy, Long> {

    Optional<RefundPolicy> findByHotelId(Long hotelId);

}
