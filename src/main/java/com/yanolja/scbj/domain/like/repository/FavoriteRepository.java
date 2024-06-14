package com.yanolja.scbj.domain.like.repository;

import com.yanolja.scbj.domain.like.entity.Favorite;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoritesResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    Favorite findByMemberIdAndProductId(Long memberId, Long productId);

    @Query("""
    SELECT new com.yanolja.scbj.domain.like.entity.dto.response.FavoritesResponse(
        p.id,
        h.hotelName,
        h.room.bedType,
        hImg.url,
        res.startDate,
        res.endDate,
        CASE
            WHEN (p.secondPrice <> 0 AND
                  (p.secondGrantPeriod <> 0) AND
                  function('DATE_SUB', res.startDate, function('INTERVAL', p.secondGrantPeriod, 'HOUR')) < function('NOW'))
            THEN p.secondPrice
            ELSE p.firstPrice
        END
    )
    FROM Favorite f
    JOIN Product p ON f.productId = p.id
    JOIN Reservation res ON p.reservation.id = res.id
    JOIN Hotel h ON res.hotel.id = h.id
    LEFT JOIN HotelRoomImage hImg ON hImg.hotel.id = h.id
    WHERE f.memberId = :memberId
    """)
    List<FavoritesResponse> findFavoritesByMemberId(@Param("memberId") Long memberId);
}
