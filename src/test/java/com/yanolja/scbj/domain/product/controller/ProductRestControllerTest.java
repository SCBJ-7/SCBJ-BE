package com.yanolja.scbj.domain.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.domain.product.dto.request.ProductPostRequest;
import com.yanolja.scbj.domain.product.dto.response.ProductPostResponse;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.service.ProductService;
import com.yanolja.scbj.global.util.SecurityUtil;
import com.yanolja.scbj.util.ControllerTestWithoutSecurityHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


class ProductRestControllerTest extends ControllerTestWithoutSecurityHelper {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private SecurityUtil securityUtil;

    @Nested
    @DisplayName("postProduct()는 ")
    class Context_postProduct {

        @Test
        @DisplayName("양도글 작성을 성공했습니다.")
        void _willSuccess() throws Exception {
            // given
            long reservationId = 1L;
            ProductPostResponse productPostResponse = ProductPostResponse.builder().productId(1L).build();
            ProductPostRequest productPostRequest = ProductPostRequest.builder().firstPrice(350000)
                .secondPrice(200000).bank("신한은행").accountNumber("1000-4400-3330")
                .secondGrantPeriod(48).build();
            given(productService.postProduct(any(Long.TYPE), any(Long.TYPE),
                any(ProductPostRequest.class))).willReturn(productPostResponse);

            // when, then
            mockMvc.perform(post("/v1/products/{reservationId}", reservationId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(productPostRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists()).andDo(print());
        }
    }
}