package com.yanolja.scbj.domain.payment.hisotryService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.yanolja.scbj.domain.payment.dto.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
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
class PurchasedHistoryServiceTest {
    @InjectMocks
    PurchasedHistoryService purchasedHistoryService;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @BeforeEach
    void setUp() {

    }

    @Nested
    @DisplayName("구매내역 조회는")
    class Context_getProduct {

        @Test
        @DisplayName("성공시 구매내역을 반환")
        void will_success() {
            // given

            Long memberId = 1L;
            Pageable pageable = PageRequest.of(1, 10);
            PurchasedHistoryResponse response = new PurchasedHistoryResponse(
                1L, // id
                LocalDateTime.now(), // createdAt
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
                purchasedHistoryService.getPurchasedBeforeCheckIn(pageable, memberId);

            //then
            assertThat(result.getContent()).containsExactly(response);
            assertThat(result).isNotNull();
        }

        void will_fail() {

        }
    }
}
