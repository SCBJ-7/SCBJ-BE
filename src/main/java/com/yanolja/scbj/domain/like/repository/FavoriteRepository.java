package com.yanolja.scbj.domain.like.repository;

import com.yanolja.scbj.domain.like.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    Favorite findByMemberIdAndProductId(Long memberId, Long productId);
}
