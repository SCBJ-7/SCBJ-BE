package com.yanolja.scbj.domain.paymentHistory.repository;

import com.yanolja.scbj.domain.paymentHistory.dto.response.CheckInAlarmResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    @Query(
        "SELECT new com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse(p.id, p.createdAt, hp.url ,h.hotelName, h.room.bedType, p.price, r.startDate, r.endDate) "
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

    @Query(
    """
        select ph from PaymentHistory ph 
        join fetch Member m
        where ph.settlement = false 
    """
    )
    List<PaymentHistory> findPaymentHistoriesWithNotSettlement();


    @Query(
        "SELECT new com.yanolja.scbj.domain.paymentHistory.dto.response.CheckInAlarmResponse(ph.id, m.id ,ph.productName, r.startDate) "+
            "FROM PaymentHistory ph " +
            "INNER JOIN ph.member m " +
            "INNER JOIN ph.product p " +
            "INNER JOIN p.reservation r " +
            "WHERE FUNCTION('DATE_FORMAT',r.startDate,'%Y-%m-%d %H:%i') = FUNCTION('DATE_FORMAT',FUNCTION('DATE_SUB', CURRENT_TIMESTAMP, 1, 'DAY'),'%Y-%m-%d %H:%i')")
    List<CheckInAlarmResponse> findPurchasedHistoriesNeedForCheckInAlarm();
}
