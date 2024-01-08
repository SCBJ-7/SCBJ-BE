package com.yanolja.scbj.domain.payment.paymentController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yanolja.scbj.domain.payment.dto.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.hisotryService.PurchasedHistoryService;
import com.yanolja.scbj.domain.payment.historycontroller.PurchasedHistoryController;
import com.yanolja.scbj.global.config.SecurityConfig;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;


@WebMvcTest(
    controllers = PurchasedHistoryController.class,
    excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
    },
    excludeAutoConfiguration = SecurityAutoConfiguration.class
)
public class PurchasedHistoryControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PurchasedHistoryService purchasedHistoryService;

    @SpyBean
    private SecurityUtil securityUtil;

    @BeforeEach
    void setup() {
        Authentication authentication =
            new UsernamePasswordAuthenticationToken(1L, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Nested
    @DisplayName("구매내역은")
    class Context_purchaseHistory {

        @Test
        @DisplayName("성공시 구매내역 리스트를 보여준다")
        void will_success() throws Exception {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<PurchasedHistoryResponse> responses = List.of(
                new PurchasedHistoryResponse(1L, LocalDateTime.now(), "wwww.yanolja.com", "A 호텔",
                    "디럭스", 20000,
                    LocalDate.now(), LocalDate.now().plusDays(2)),
                new PurchasedHistoryResponse(2L, LocalDateTime.now().minusDays(3),
                    "wwww.yanolja.com", "B 호텔", "스텐다드",
                    15000, LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
            );
            Page<PurchasedHistoryResponse> response =
                new PageImpl<>(responses, pageable, responses.size());

            given(purchasedHistoryService.getUsersPurchasedHistory(any(Pageable.class),
                anyLong())).willReturn(response);

            // when
            MvcResult result = mockMvc.perform(get("/v1/members/purchased-history")
                    .param("page", "0")
                    .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

            // then
            String content = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
            assertThat(content).contains("조회에 성공하였습니다.");
            assertThat(content).contains("A 호텔");
            assertThat(content).contains("디럭스");
        }
    }
}



