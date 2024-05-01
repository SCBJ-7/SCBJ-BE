package com.yanolja.scbj.domain.favorite.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.like.controller.FavoriteRestController;
import com.yanolja.scbj.domain.like.entity.dto.request.FavoriteRegisterRequest;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteDeleteResponse;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteRegisterResponse;
import com.yanolja.scbj.domain.like.service.FavoriteService;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
    controllers = FavoriteRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class FavoriteControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private FavoriteService favoriteService;
    @MockBean
    private SecurityUtil securityUtil;

    @Nested
    @DisplayName("찜 등록은")
    class RegisterFavorite {

        @Test
        @DisplayName("성공 시 201 상태 코드와 성공 메시지를 반환한다.")
        void registerFavorite_success() throws Exception {
            // given
            Long productId = 1L;
            FavoriteRegisterRequest request = FavoriteRegisterRequest.builder()
                .favoriteStatement(true)
                .build();

            FavoriteRegisterResponse response = FavoriteRegisterResponse.builder()
                .favoriteId(1L)
                .likeStatement(true)
                .build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(favoriteService.register(1L, productId, request)).willReturn(response);

            // when
            ResultActions result = mvc.perform(post("/v1/favorites/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            //then
            result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("좋아요 등록에 성공하였습니다.")))
                .andExpect(jsonPath("$.data.favoriteId", is(1)))
                .andExpect(jsonPath("$.data.likeStatement", is(true)))
                .andDo(print());
        }
    }


    @Nested
    @DisplayName("DELETE /v1/favorites/{product_id}")
    class DeleteFavorite {

        @Test
        @DisplayName("성공 시 200 상태 코드와 성공 메시지를 반환한다.")
        void deleteFavorite_success() throws Exception {
            // given
            Long productId = 1L;
            FavoriteDeleteResponse response = FavoriteDeleteResponse.builder().productId(productId).build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(favoriteService.remove(1L, productId)).willReturn(response);

            // when
            ResultActions result = mvc.perform(delete("/v1/favorites/" + productId));

            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("삭제에 성공하였습니다")))
                .andExpect(jsonPath("$.data.productId",is(1)))
                .andDo(print());

        }
    }
}
