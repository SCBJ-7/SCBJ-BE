package com.yanolja.scbj.domain.like.service;

import com.yanolja.scbj.domain.like.dto.response.FavoriteDeleteResponse;
import com.yanolja.scbj.domain.like.dto.response.FavoritesResponse;
import com.yanolja.scbj.domain.like.entity.Favorite;
import com.yanolja.scbj.domain.like.exception.FavoriteDeleteFailException;
import com.yanolja.scbj.domain.like.repository.FavoriteRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;

    @Transactional
    public void register(Long memberId,
                         Long productId,
                         boolean favoriteStatus
    ) {
        Favorite favorite = FavoriteMapper.toFavorite(memberId, productId, favoriteStatus);
        save(favorite);
    }

    private Favorite save(Favorite favorite) {
        try {
            return favoriteRepository.save(favorite);
        } catch (DataIntegrityViolationException e) {
            log.error("무결성 조건 위반됨:{},{}", e.getMessage(), e.getCause(), e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_SAVE_NOT_AVAILABLE);
        } catch (EntityExistsException e) {
            log.error("좋아요 엔티티 중복됨:{},{}", e.getMessage(), e.getCause(), e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_SAVE_NOT_AVAILABLE);
        }
    }


    @Transactional
    public FavoriteDeleteResponse remove(Long memberId,
                                         Long productId) {
        Favorite favorite = getByMemberIdAndProductId(memberId, productId);
        delete(favorite);
        return FavoriteMapper.toFavoriteDeleteResponse(favorite);
    }

    private Favorite getByMemberIdAndProductId(Long memberId, Long productId) {
        try {
            return favoriteRepository.findByMemberIdAndProductId(memberId, productId);
        } catch (EntityNotFoundException e) {
            log.error("좋아요 엔티티 찾기가 불가함:{},{}", e.getMessage(), e.getCause(), e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_CANNOT_FIND);
        }
    }

    private void delete(Favorite favorite) {
        try {
            favoriteRepository.delete(favorite);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 인자가 전달됨:{},{}", e.getMessage(), e.getCause(), e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_DELETE_NOT_AVAILABLE);
        }
    }

    @Transactional(readOnly = true)
    public Page<FavoritesResponse> getFavorites(Long memberId,
                                                Pageable pageable) {
        Page<FavoritesResponse> response =
            favoriteRepository.findFavoritesByMemberId(memberId, pageable);
        return response.isEmpty() ? Page.empty(pageable) : response;
    }
}
