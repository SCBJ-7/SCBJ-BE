package com.yanolja.scbj.domain.like.controller;

import com.yanolja.scbj.domain.like.entity.dto.request.FavoriteRegisterRequest;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteDeleteResponse;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteRegisterResponse;
import com.yanolja.scbj.domain.like.service.FavoriteService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/favorites") //todo product/favorites
public class FavoriteRestController {

    private final FavoriteService favoriteService;
    private final SecurityUtil securityUtil;


    @PostMapping("/{product_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDTO<FavoriteRegisterResponse> register(
        @PathVariable("product_id") Long productId,
        @Valid @RequestBody FavoriteRegisterRequest favoriteRegisterRequest) {
        return ResponseDTO.res(favoriteService.register(securityUtil.getCurrentMemberId(),
            productId,
            favoriteRegisterRequest),
            "좋아요 등록에 성공하였습니다.");
    }

    @DeleteMapping("/{product_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<FavoriteDeleteResponse> delete(@PathVariable("product_id") Long productId) {
        return ResponseDTO.res(favoriteService.delete(securityUtil.getCurrentMemberId(),
                productId),
            "삭제에 성공하였습니다");
    }

}
