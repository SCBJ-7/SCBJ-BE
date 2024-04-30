package com.yanolja.scbj.domain.like.service;

import com.yanolja.scbj.domain.like.entity.Favorite;
import com.yanolja.scbj.domain.like.entity.dto.request.FavoriteRegisterRequest;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteDeleteResponse;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteRegisterResponse;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.product.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FavoriteMapper {

    public static Favorite toFavorite(Long memberId,
                                      Long productId,
                                      FavoriteRegisterRequest favoriteRegisterRequest) {
        return Favorite.builder()
            .memberId(memberId)
            .productId(productId)
            .favoriteStatement(favoriteRegisterRequest.favoriteStatement())
            .build();
    }

    public static FavoriteRegisterResponse toFavoriteRegisterResponse(Favorite favorite) {
        return FavoriteRegisterResponse.builder()
            .favoriteId(favorite.getId())
            .likeStatement(favorite.isFavoriteStatement())
            .build();

    }

    public static FavoriteDeleteResponse toFavoriteDeleteResponse(Favorite favorite) {
        return FavoriteDeleteResponse.builder()
            .productId(favorite.getProductId())
            .build();
    }
}
