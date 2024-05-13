package com.yanolja.scbj.domain.favorite.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.like.controller.FavoriteRestController;
import com.yanolja.scbj.domain.like.entity.dto.request.FavoriteRegisterRequest;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteDeleteResponse;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoriteRegisterResponse;
import com.yanolja.scbj.domain.like.entity.dto.response.FavoritesResponse;
import com.yanolja.scbj.domain.like.service.FavoriteService;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.List;
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

            // when
            ResultActions result = mvc.perform(post("/v1/favorites/" + productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

            //then
            result.andExpect(status().isCreated())
                .andDo(print());
        }
    }


    @Nested
    @DisplayName("찜 삭제는")
    class DeleteFavorite {

        @Test
        @DisplayName("성공 시 200 상태 코드와 성공 메시지를 반환한다.")
        void deleteFavorite_success() throws Exception {
            // given
            Long productId = 1L;
            FavoriteDeleteResponse response =
                FavoriteDeleteResponse.builder().productId(productId).build();

            given(securityUtil.getCurrentMemberId()).willReturn(1L);
            given(favoriteService.remove(1L, productId)).willReturn(response);

            // when
            ResultActions result = mvc.perform(delete("/v1/favorites/" + productId));

            //then
            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("삭제에 성공하였습니다")))
                .andExpect(jsonPath("$.data.productId", is(1)))
                .andDo(print());

        }
    }

    @Nested
    @DisplayName("찜 조회는")
    class Context_Find_Favorites {
        @Test
        public void testGetFavorites() throws Exception {
            when(securityUtil.getCurrentMemberId()).thenReturn(1L);
            List<FavoritesResponse> mockFavorites = List.of(
                FavoritesResponse.builder().
                    hotelName("시그니엘")
                    .roomType("스탠다드")
                    .imageUrl("https://example.com/image2.jpg")
                    .checkInDate(LocalDateTime.of(2024, 5, 20, 14, 0))
                    .checkOutDate(LocalDateTime.of(2024, 5, 23, 10, 0))
                    .price(90000)
                    .build()
                ,
                FavoritesResponse.builder().
                    hotelName("롯데")
                    .roomType("디럭스")
                    .imageUrl("https://example.com/image2.jpg")
                    .checkInDate(LocalDateTime.of(2024, 5, 20, 14, 0))
                    .checkOutDate(LocalDateTime.of(2024, 5, 23, 10, 0))
                    .price(90000)
                    .build()
            );

            when(favoriteService.getFavorites(anyLong())).thenReturn(mockFavorites);

            ResultActions result = mvc.perform(get("/v1/favorites"));

            result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("조회에 성공하였습니다"))
                .andExpect(jsonPath("$.data[0].hotelName").value("시그니엘"))
                .andExpect(jsonPath("$.data[0].roomType").value("스탠다드"))
                .andExpect(jsonPath("$.data[1].hotelName").value("롯데"))
                .andExpect(jsonPath("$.data[1].price").value(90000));
        }
    }
}
