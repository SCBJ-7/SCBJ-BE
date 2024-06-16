package com.yanolja.scbj.domain.like.repository;

import com.yanolja.scbj.domain.like.entity.Favorite;
import com.yanolja.scbj.domain.like.dto.response.FavoritesResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    Favorite findByMemberIdAndProductId(Long memberId, Long productId);


    @Query(value = """
        SELECT p.id AS id, 
               h.hotel_name AS hotelName, 
               h.bed_type AS bedType, 
               MAX(hImg.url) AS imageUrl, 
               res.start_date AS checkInDate, 
               res.end_date AS checkOutDate,
               CASE
                   WHEN p.second_price <> 0 AND p.second_grant_period <> 0 AND DATE_SUB(res.start_date, INTERVAL p.second_grant_period HOUR) < NOW()
                   THEN p.second_price
                   ELSE p.first_price
               END AS price
        FROM favorite f
        JOIN product p ON f.product_id = p.id
        JOIN reservation res ON p.reservation_id = res.id
        JOIN hotel h ON res.hotel_id = h.id
        LEFT JOIN hotel_room_image hImg ON hImg.hotel_id = h.id
        WHERE f.member_id = :memberId
        GROUP BY p.id, h.hotel_name, h.bed_type, res.start_date, res.end_date, p.second_price, p.first_price, p.second_grant_period;
        """,
        countQuery = "SELECT COUNT(*) FROM favorite f WHERE f.member_id = :memberId",
        nativeQuery = true)
    Page<FavoritesResponse> findFavoritesByMemberId(@Param("memberId") Long memberId, Pageable pageable);


}
