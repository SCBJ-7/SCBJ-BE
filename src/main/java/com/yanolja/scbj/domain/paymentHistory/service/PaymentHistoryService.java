package com.yanolja.scbj.domain.paymentHistory.service;

import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificSaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.paymentHistory.exception.PaymentHistoryNotFoundException;
import com.yanolja.scbj.domain.paymentHistory.exception.SaleHistoryNotFoundException;
import com.yanolja.scbj.domain.paymentHistory.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentHistoryDtoConverter paymentHistoryDtoConverter;
    private final SaleHistoryDtoConverter saleHistoryDtoConverter;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final ProductRepository productRepository;

    public List<PurchasedHistoryResponse> getUsersPurchasedHistory( Long id) {
        List<PurchasedHistoryResponse> response =
            paymentHistoryRepository.findPurchasedHistoriesByMemberId(id);
        return response.isEmpty() ? Collections.emptyList() : response;
    }

    public Page<SaleHistoryResponse> getUsersSaleHistory(Pageable pageable, Long id) {
        Page<SaleHistoryResponse> response =
            productRepository.findSaleHistoriesByMemberId(id, pageable);
        return response.isEmpty() ? Page.empty() : response;
    }

    @Transactional(readOnly = true)
    public SpecificPurchasedHistoryResponse getSpecificPurchasedHistory(Long memberId,
        Long purchaseHistoryId) {

        PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndMemberId(
                purchaseHistoryId, memberId)
            .orElseThrow(() -> new PaymentHistoryNotFoundException(ErrorCode.PURCHASE_LOAD_FAIL));

        return paymentHistoryDtoConverter.toSpecificPurchasedHistoryResponse(paymentHistory);
    }


    public SpecificSaleHistoryResponse getSpecificSaleHistory(Long memberId, Long saleHistoryId) {

        PaymentHistory saleHistoryDetail =
            paymentHistoryRepository.findByIdAndMemberId(saleHistoryId, memberId).orElseThrow(
                () -> new SaleHistoryNotFoundException(ErrorCode.SALE_DETAIL_LOAD_FAIL));

        return saleHistoryDtoConverter.toSpecificSaleHistoryResponse(saleHistoryDetail);
    }
}
