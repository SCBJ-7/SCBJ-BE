package com.yanolja.scbj.domain.paymentHistory.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PreparePaymentResponse;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentService;
import com.yanolja.scbj.domain.paymentHistory.service.paymentApi.KaKaoPaymentService;
import com.yanolja.scbj.domain.paymentHistory.service.paymentApi.PaymentApiService;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.time.LocalDateTime;
import java.util.Map;
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
    controllers = PaymentRestController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
class PaymentRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private SecurityUtil securityUtil;

//    @MockBean
//    private PaymentApiService paymentApiService;

    @MockBean
    private Map<String, PaymentApiService> paymentApiServiceMap;

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

    @Nested
    @DisplayName("카카오페이로 결제는 ")
    class Context_PaymentRequest{
        
        @Test
        @DisplayName("요청 성공 시 카카오페이 결제 url을 반환한다.")
        void _will_success_request() throws Exception {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("asgasdaf")
                .build();

            Product product = Product.builder()
                .id(1L)
                .member(member)
                .build();

            PaymentReadyRequest paymentReadyRequest = PaymentReadyRequest.builder()
                .customerEmail("agasd@naver.com")
                .customerName("agasda")
                .customerPhoneNumber("01012340055")
                .build();

            String url = "test.com";
            PreparePaymentResponse preparePaymentResponse = new PreparePaymentResponse(url);

            KaKaoPaymentService kaKaoPaymentService = mock(KaKaoPaymentService.class);
            PaymentApiService paymentApiService = mock(KaKaoPaymentService.class);

            given(paymentApiServiceMap.get(any())).willReturn(kaKaoPaymentService);
            given(kaKaoPaymentService.preparePayment(any(Long.TYPE), any(Long.TYPE),
                any(PaymentReadyRequest.class))).willReturn(preparePaymentResponse);

            // when
            ResultActions result = mvc.perform(
                post("/v1/products/" + product.getId() + "/payments?paymentType=kakaoPaymentService")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(new ObjectMapper().writeValueAsString(paymentReadyRequest)));

            // then
            result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.url", is(url)));
        }

        @Test
        @DisplayName("승인 성공 시 성공 메시지를 반환한다.")
        void _will_success_payment() throws Exception {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("asgasdaf")
                .build();

            KaKaoPaymentService kaKaoPaymentService = mock(KaKaoPaymentService.class);
            PaymentApiService paymentApiService = mock(KaKaoPaymentService.class);

            given(paymentApiServiceMap.get(any())).willReturn(kaKaoPaymentService);
            doNothing().when(kaKaoPaymentService).approvePayment(any(), any());

            // when
            ResultActions result = mvc.perform(
                get("/v1/products/pay-success?memberId=" + member.getId()
                    + "&paymentType=kakaoPaymentService&pg_token=gasdagasdasfasgf"));

            // then
            result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("결제에 성공했습니다.")));
        }

        @Test
        @DisplayName("취소 시 취소 성공 메시지를 반환한다.")
        void _will_cancel_payment() throws Exception {
            // given
            Member member = Member.builder()
                .id(1L)
                .name("asgasdaf")
                .build();

            KaKaoPaymentService kaKaoPaymentService = mock(KaKaoPaymentService.class);
            PaymentApiService paymentApiService = mock(KaKaoPaymentService.class);

            given(paymentApiServiceMap.get(any())).willReturn(kaKaoPaymentService);
            doNothing().when(kaKaoPaymentService).cancelPayment(any());

            // when
            ResultActions result = mvc.perform(
                get("/v1/products/pay-cancel?memberId=" + member.getId()
                    + "&paymentType=kakaoPaymentService"));

            // then
            result.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.message", is("결제에 실패했습니다.")));
        }

    }
}
