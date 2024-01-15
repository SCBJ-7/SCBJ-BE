package com.yanolja.scbj.domain.payment.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.domain.payment.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.payment.service.PaymentService;
import com.yanolja.scbj.global.config.SecurityConfig;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(
    controllers = PaymentRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class PaymentRestControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PaymentService paymentService;
    @MockBean
    private SecurityUtil securityUtil;

    @Nested
    @DisplayName("결제 페이지 조회는 ")
    class Context_PaymentPageFind {

        @Test
        @DisplayName("성공 시 결제할 상품의 정보를 반환한다.")
        void _will_success() throws Exception {
            // given
            Long targetProductId = 1L;

            PaymentPageFindResponse findResponse = PaymentPageFindResponse.builder()
                .hotelName("양도 호텔")
                .hotelImage("asdasfnaijsndijkv.jpg")
                .roomName("룸1")
                .standardPeople(2)
                .maxPeople(4)
                .checkInDateTime(LocalDateTime.now())
                .checkOutDateTime(LocalDateTime.now())
                .originalPrice(200000)
                .salePrice(100000)
                .build();

            given(paymentService.getPaymentPage(targetProductId)).willReturn(findResponse);

            // when
            ResultActions result = mvc.perform(
                get("/v1/products/" + targetProductId + "/payments"));

            // then
            result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.hotelName", is("양도 호텔")));
        }
    }
}
