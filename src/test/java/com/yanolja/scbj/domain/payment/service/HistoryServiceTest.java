package com.yanolja.scbj.domain.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.payment.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class HistoryServiceTest {
    @InjectMocks
    HistoryService historyService;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {

    }

    @Nested
    @DisplayName("구매내역 조회는")
    class Context_Purchased_getProduct {

        @Test
        @DisplayName("성공시 구매내역을 반환")
        void will_success() {
            // given
            Long memberId = 1L;
            Pageable pageable = PageRequest.of(1, 10);
            PurchasedHistoryResponse response = new PurchasedHistoryResponse(
                1L, // id
                LocalDateTime.now(), // createdAt
                "www.naver.co.kr",
                "서울 호텔", // name
                "디럭스", // roomType
                1500000, // price
                LocalDate.now().plusDays(3), // checkInDate
                LocalDate.now().plusDays(5) // checkOutDate
            );
            Page<PurchasedHistoryResponse> expectedPage =
                new PageImpl<>(List.of(response), pageable, 1);

            given(paymentHistoryRepository.findPurchasedHistoriesByMemberId(memberId,
                pageable)).willReturn(expectedPage);

            // when
            Page<PurchasedHistoryResponse> result =
                historyService.getUsersPurchasedHistory(pageable, memberId);

            //then
            assertThat(result.getContent()).containsExactly(response);
            assertThat(result).isNotNull();
        }

        void will_fail() {
        }
    }


    @Nested
    @DisplayName("판매내역 조회는")
    class Context__getSaleProductHistory {


        @Test
        @DisplayName("성공시 판매내역을 반환")
        void will_success() {
            //given
            Long memberId = 1L;
            Pageable pageable = PageRequest.of(1, 10);
            SaleHistoryResponse response = new SaleHistoryResponse(
                1L,
                "롯데 시그니엘 호텔",
                "http://example.com/hotel-room-image1.jpg",
                "더블 베드",
                200000,
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 1, 2),
                "판매중");

            Page<SaleHistoryResponse> expectedPage =
                new PageImpl<>(List.of(response), pageable, 1);

            given(productRepository.findSaleHistoriesByMemberId(memberId, pageable)).willReturn(
                expectedPage);

            //when
            Page<SaleHistoryResponse> result =
                productRepository.findSaleHistoriesByMemberId(memberId, pageable);


            //then
            assertThat(result.getContent()).containsExactly(response);
            assertThat(result).isNotNull();

        }
    }
}
