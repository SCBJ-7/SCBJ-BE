package com.yanolja.scbj.domain.paymentHistory.controller;


import com.yanolja.scbj.domain.paymentHistory.dto.response.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificPurchasedHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.SpecificSaleHistoryResponse;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentHistoryService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.util.List;
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

    public PaymentHistoryRestController(PaymentHistoryService paymentHistoryService,
                                        SecurityUtil securityUtil) {
        this.paymentHistoryService = paymentHistoryService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/purchased-history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<List<PurchasedHistoryResponse>> getPurchasedHistories() {
        Long memberId = securityUtil.getCurrentMemberId();
        List<PurchasedHistoryResponse> response =
            paymentHistoryService.getUsersPurchasedHistory(memberId);
        return ResponseDTO.res(response, "조회에 성공하였습니다.");
    }


    @GetMapping("/sale-history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<List<SaleHistoryResponse>> getSaleHistories() {
        Long memberId = securityUtil.getCurrentMemberId();
        List<SaleHistoryResponse> response =
            paymentHistoryService.getUsersSaleHistory(memberId);
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


    @GetMapping("/sale-history/{saleHistory_id}/{isPayment_Id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<SpecificSaleHistoryResponse> getSpecificSaleHistory(
        @PathVariable("saleHistory_id") Long saleId,
        @PathVariable("isPayment_Id") boolean isPaymentId
    ) {
        return ResponseDTO.res(
            paymentHistoryService.getSpecificSaleHistory(securityUtil.getCurrentMemberId(),
                saleId, isPaymentId), "판매 내역 상세 조회를 성공했습니다");
    }
}
