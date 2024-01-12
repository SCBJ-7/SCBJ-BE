package com.yanolja.scbj.domain.payment.repository;

import com.yanolja.scbj.domain.payment.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
    @Query(
        "SELECT new com.yanolja.scbj.domain.payment.dto.response.PurchasedHistoryResponse(p.id, p.createdAt, hp.url ,h.hotelName, h.room.bedType, p.price, r.startDate, r.endDate) "
            +
            "FROM PaymentHistory p " +
            "JOIN p.product pr " +
            "JOIN pr.reservation r " +
            "JOIN r.hotel h " +
            "LEFT JOIN HotelRoomImage hp ON hp.hotel.id = h.id " +
            "WHERE p.member.id = :memberId "
    )
    Page<PurchasedHistoryResponse> findPurchasedHistoriesByMemberId(
        @Param("memberId") Long memberId, Pageable pageable);

    Optional<PaymentHistory> findByIdAndMemberId(Long paymentHistoryId, Long memberId);
}
