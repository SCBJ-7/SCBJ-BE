package com.yanolja.scbj.domain.payment.controller;

import com.yanolja.scbj.domain.payment.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.service.PaymentHistoryService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/members")
public class PaymentHistoryRestController {

    private final PaymentHistoryService paymentHistoryService;
    private final SecurityUtil securityUtil;

    public PaymentHistoryRestController(PaymentHistoryService paymentHistoryService, SecurityUtil securityUtil) {
        this.paymentHistoryService = paymentHistoryService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/purchased-history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Page<PurchasedHistoryResponse>> getPurchasedHistories(
        @PageableDefault(page = 1) Pageable pageable
    ) {
        Long memberId = securityUtil.getCurrentMemberId();
        Page<PurchasedHistoryResponse> response =
            paymentHistoryService.getUsersPurchasedHistory(pageable, memberId);
        return ResponseDTO.res(response, "조회에 성공하였습니다.");
    }


    @GetMapping("/sale-history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Page<SaleHistoryResponse>> getSaleHistories(
        @PageableDefault(page = 1) Pageable pageable
    ) {
        Long memberId = securityUtil.getCurrentMemberId();
        Page<SaleHistoryResponse> response =
            paymentHistoryService.getUsersSaleHistory(pageable, memberId);
        return ResponseDTO.res(response, "조회에 성공하였습니다.");
    }

    @GetMapping("/purchased-history/{paymentHistory_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<SpecificPurchasedHistoryResponse> getSpecificPurchasedHistory(
        @PathVariable("paymentHistory_id") Long paymentHistoryId) {
        return ResponseDTO.res(
            paymentHistoryService.getSpecificPurchasedHistory(securityUtil.getCurrentMemberId(),
                paymentHistoryId), "구매 내역 상세 조회를 성공했습니다.");
    }
}
