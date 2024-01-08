package com.yanolja.scbj.domain.payment.hisotryService;

import com.yanolja.scbj.domain.payment.dto.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PurchasedHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public Page<PurchasedHistoryResponse> getUsersPurchasedHistory(Pageable pageable, Long id) {
        Page<PurchasedHistoryResponse> response =
            paymentHistoryRepository.findPurchasedHistoriesByMemberId(id, pageable);
        return response.isEmpty() ? Page.empty() : response;
    }
}
