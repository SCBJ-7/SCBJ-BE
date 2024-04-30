package com.yanolja.scbj.domain.like.service;

import com.yanolja.scbj.domain.like.entity.Favorite;
import com.yanolja.scbj.domain.like.entity.dto.request.FavoriteRegisterRequest;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteDeleteResponse;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteRegisterResponse;
import com.yanolja.scbj.domain.like.exception.FavoriteDeleteFailException;
import com.yanolja.scbj.domain.like.repository.FavoriteRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;


    @Transactional
    public FavoriteRegisterResponse register(Long memberId,
                                             Long productId,
                                             FavoriteRegisterRequest favoriteRegisterRequest
    ) {
        Favorite favorite = FavoriteMapper.toFavorite(memberId, productId, favoriteRegisterRequest);
        Favorite save = save(favorite);
        return FavoriteMapper.toFavoriteRegisterResponse(save);
    }

    private Favorite save(Favorite favorite) {
        try {
            return favoriteRepository.save(favorite);
        } catch (DataIntegrityViolationException e) {
            log.error("무결성 조건 위반됨:{}",e.getMessage(),e.getCause(),e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_SAVE_NOT_AVAILABLE);
        } catch (EntityExistsException e){
            log.error("좋아요 엔티티 중복됨:{}",e.getMessage(),e.getCause(),e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_SAVE_NOT_AVAILABLE);
        }
    }


    @Transactional
    public FavoriteDeleteResponse delete(Long memberId,
                                         Long productId) {
        Favorite favorite = favoriteRepository.findByMemberIdAndProductId(memberId, productId);
        delete(favorite);
        return FavoriteMapper.toFavoriteDeleteResponse(favorite);
    }

    private void delete(Favorite favorite) {
        try {
            favoriteRepository.delete(favorite);
        } catch (IllegalArgumentException e) {
            log.error("유효하지 않은 인자가 전달됨:{}", e.getMessage(), e.getCause(),e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_DELETE_NOT_AVAILABLE);
        } catch (EntityNotFoundException e) {
            log.error("좋아요 엔티티 찾기가 불가함:{}", e.getMessage(), e.getCause(),e);
            throw new FavoriteDeleteFailException(ErrorCode.FAVORITE_CANNOT_FIND);
        }
    }
}
