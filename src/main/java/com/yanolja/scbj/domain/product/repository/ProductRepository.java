package com.yanolja.scbj.domain.product.repository;

import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.product.entity.Product;
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
    Page<SaleHistoryResponse> findSaleHistoriesByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select p from Product p "
        + "join fetch p.reservation r "
        + "join fetch r.hotel h "
        + "join fetch h.hotelRoomImageList hi "
        + "join fetch h.hotelRoomPrice hp "
        + "where p.id = :productId")
    Optional<Product> findProductById(@Param("productId") Long productId);

}
