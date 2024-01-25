package com.yanolja.scbj.domain.paymentHistory.repository;

import com.yanolja.scbj.domain.alarm.dto.CheckInAlarmResponse;
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
    List<PurchasedHistoryResponse> findPurchasedHistoriesByMemberId(
        @Param("memberId") Long memberId);

    Optional<PaymentHistory> findByIdAndMemberId(Long paymentHistoryId, Long memberId);

    @Query(
    """
        select ph from PaymentHistory ph 
        join fetch ph.member m
        where ph.settlement = false 
    """
    )
    List<PaymentHistory> findPaymentHistoriesWithNotSettlement();


    @Query(name ="find_check_in_alarm",nativeQuery = true)
    List<CheckInAlarmResponse> findPurchasedHistoriesNeedForCheckInAlarm();
}
