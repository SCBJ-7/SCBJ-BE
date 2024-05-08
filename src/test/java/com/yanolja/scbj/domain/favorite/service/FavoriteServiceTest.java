package com.yanolja.scbj.domain.favorite.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

import com.yanolja.scbj.domain.like.entity.Favorite;
import com.yanolja.scbj.domain.like.exception.FavoriteDeleteFailException;
import com.yanolja.scbj.domain.like.repository.FavoriteRepository;
import com.yanolja.scbj.domain.like.service.FavoriteService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

public class FavoriteServiceTest {
    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    Favorite setFavorite() {
        return Favorite.builder()
            .id(1L)
            .productId(1L)
            .memberId(1L)
            .favoriteStatement(true)
            .build();
    }

    @Nested
    @DisplayName("찜 등록과 삭제는")
    class Context_Favorite_register_delete {

        @Test
        void registerFavorite_fail_dueToDataIntegrityViolation() {
            // given
            Long memberId = 1L;
            Long productId = 1L;
            boolean request = true;
            when(favoriteRepository.save(any(Favorite.class))).thenThrow(
                new DataIntegrityViolationException("Violation"));

            // when & then
            assertThrows(FavoriteDeleteFailException.class, () -> {
                favoriteService.register(memberId, productId, request);
            });
        }

        @Test
        void deleteFavorite_fail_dueToEntityNotFound() {
            // given
            Long memberId = 1L;
            Long productId = 1L;


            //when
            when(favoriteRepository.findByMemberIdAndProductId(memberId, productId)).thenThrow(
                new EntityNotFoundException());

            //then
            assertThrows(FavoriteDeleteFailException.class, () -> {
                favoriteService.remove(memberId, productId);
            });
        }
    }
}
