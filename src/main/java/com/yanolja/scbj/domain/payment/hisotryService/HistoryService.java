package com.yanolja.scbj.domain.payment.hisotryService;

import com.yanolja.scbj.domain.payment.dto.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ProductRepository productRepository;

    public Page<PurchasedHistoryResponse> getUsersPurchasedHistory(Pageable pageable, Long id) {
        Page<PurchasedHistoryResponse> response =
            paymentHistoryRepository.findPurchasedHistoriesByMemberId(id, pageable);
        return response.isEmpty() ? Page.empty() : response;
    }

    public Page<SaleHistoryResponse> getUsersSaleHistory(Pageable pageable, Long id) {
        Page<SaleHistoryResponse> response =
            productRepository.findSaleHistoriesByMemberId(id, pageable);
        return response.isEmpty() ? Page.empty() : response;
    }
}
