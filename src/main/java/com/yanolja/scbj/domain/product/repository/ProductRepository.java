package com.yanolja.scbj.domain.product.repository;

import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("""
       SELECT new com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse(
           p.id,  
           h.hotelName, 
           hImg.url, 
           h.room.bedType, 
           p.firstPrice,
           p.secondPrice, 
           r.startDate, 
           r.endDate, 
           (CASE 
                WHEN ph.id IS NOT NULL THEN 
                    (CASE 
                         WHEN ph.settlement = true THEN '정산완료' 
                         ELSE '거래완료' 
                     END) 
                ELSE 
                    (CASE 
                         WHEN r.endDate < CURRENT_DATE THEN '판매만료' 
                         ELSE '판매중' 
                     END) 
           END)
       ) 
       FROM Product p 
       LEFT JOIN p.reservation r 
       JOIN r.hotel h 
       LEFT JOIN HotelRoomImage hImg ON hImg.hotel.id = h.id
       LEFT JOIN p.paymentHistory ph 
       WHERE p.member.id = :memberId
       """)
    List<SaleHistoryResponse> findSaleHistoriesByMemberId(@Param("memberId") Long memberId);

    @Query("select p from Product p "
        + "join fetch p.reservation r "
        + "join fetch r.hotel h "
        + "join fetch h.hotelRoomImageList hi "
        + "join fetch h.hotelRoomPrice hp "
        + "where p.id = :productId")
    Optional<Product> findProductById(@Param("productId") Long productId);


    @Query("""
    FROM Product p
    JOIN FETCH p.reservation r
    JOIN FETCH r.hotel h
    LEFT JOIN FETCH p.paymentHistory ph
    WHERE h.hotelMainAddress = :city AND ph.id IS NULL
""")
    List<Product> findProductByCity(@Param("city") String city);


    @Query(value = """
    SELECT * 
    FROM product p
    JOIN reservation r ON p.reservation_id = r.id
    JOIN hotel h ON r.hotel_id = h.id
    LEFT JOIN paymentHistory ph ON p.payment_history_id = ph.id
    WHERE DAYOFWEEK(r.start_date) IN (6, 7, 1)
    AND r.start_date BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 21 DAY)
    AND ph.id IS NULL
    """, nativeQuery = true)
    List<Product> findProductByWeekend();

    @Query("SELECT p FROM Product p JOIN FETCH p.reservation r WHERE r.id = :reservationId")
    Optional<Product> findByReservationId(@Param("reservationId") Long reservationId);

}
