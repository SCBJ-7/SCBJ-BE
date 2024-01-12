package com.yanolja.scbj.domain.payment.service;

import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.exception.MemberNotFoundException;
import com.yanolja.scbj.domain.member.repository.MemberRepository;
import com.yanolja.scbj.domain.payment.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.entity.PaymentHistory;
import com.yanolja.scbj.domain.payment.exception.PaymentHistoryNotFoundException;
import com.yanolja.scbj.domain.payment.repository.PaymentHistoryRepository;
import com.yanolja.scbj.domain.product.repository.ProductRepository;
import com.yanolja.scbj.domain.reservation.repository.ReservationRepository;
import com.yanolja.scbj.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentHistoryDtoConverter paymentHistoryDtoConverter;
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

    @Transactional(readOnly = true)
    public SpecificPurchasedHistoryResponse getSpecificPurchasedHistory(Long memberId,
        Long purchaseHistoryId) {

        PaymentHistory paymentHistory = paymentHistoryRepository.findByIdAndMemberId(
                purchaseHistoryId, memberId)
            .orElseThrow(() -> new PaymentHistoryNotFoundException(ErrorCode.PURCHASE_LOAD_FAIL));

        return paymentHistoryDtoConverter.toSpecificPurchasedHistoryResponse(paymentHistory);
    }
}
