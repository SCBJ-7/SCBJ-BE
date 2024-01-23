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
         SELECT
                p.id as pid,
                p.first_price as pfirst_price,
                p.second_grant_period as psecond_grant_period,
                p.second_price as psecond_price,
                p.account_number as paccount_number,
                p.bank as pbank,
                p.stock as pstock,
                p.created_at as pcreated_at,
                p.updated_at as pupdated_at,
                p.deleted_at as pdeleted_at,
            
                r.id as rid,
                r.start_date as rstart_date,
                r.end_date as rend_date,
                r.purchase_price as rpurchase_price,
                r.created_at as rcreated_at,
                r.updated_at as rupdated_at,
                r.deleted_at as rdeleted_at,
            
                h.id as hid,
                h.hotel_name as hhotel_name,
                h.hotel_main_address as hhotel_main_address,
                h.hotel_detail_address as hhotel_detail_address,
                h.hotel_info_url as hhotel_info_url,
                h.created_at as hcreated_at,
                h.updated_at as hupdated_at,
                h.deleted_at as hdeleted_at,
            
                ph.id as phid,
                ph.product_name as phproduct_name,
                ph.price as phprice,
                ph.customer_name as phcustomer_name,
                ph.customer_email as phcustomer_email,
                ph.customer_phone_number as phcustomer_phone_number,
                ph.payment_type as php_payment_type,
                ph.settlement as phsettlement,
                ph.created_at as phcreated_at,
                ph.updated_at as phupdated_at,
                ph.deleted_at as phdeleted_at
        FROM product p
        JOIN reservation r ON p.reservation_id = r.id
        JOIN hotel h ON r.hotel_id = h.id
        LEFT JOIN payment_history ph ON p.id = ph.product_id
        WHERE DAYOFWEEK(r.start_date) IN (6, 7, 1)
        AND r.start_date BETWEEN CURRENT_DATE AND DATE_ADD(CURRENT_DATE, INTERVAL 21 DAY)
        AND ph.id IS NULL
        """, nativeQuery = true)
    List<Product> findProductByWeekend();

    @Query("SELECT p FROM Product p JOIN FETCH p.reservation r WHERE r.id = :reservationId")
    Optional<Product> findByReservationId(@Param("reservationId") Long reservationId);

}
