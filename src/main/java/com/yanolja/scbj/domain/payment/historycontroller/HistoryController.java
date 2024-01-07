package com.yanolja.scbj.domain.payment.historycontroller;

import com.yanolja.scbj.domain.payment.dto.PurchasedHistoryResponse;
import com.yanolja.scbj.domain.payment.dto.SaleHistoryResponse;
import com.yanolja.scbj.domain.payment.hisotryService.HistoryService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/members/")
public class HistoryController {

    private final HistoryService historyService;
    private final SecurityUtil securityUtil;

    @GetMapping("purchased-history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Page<PurchasedHistoryResponse>> getPurchasedHistories(
        @PageableDefault(page = 1) Pageable pageable
    ) {
        Long memberId = securityUtil.getCurrentMemberId();
        Page<PurchasedHistoryResponse> response =
            historyService.getUsersPurchasedHistory(pageable, memberId);
        return ResponseDTO.res(response, "조회에 성공하였습니다.");
    }


    @GetMapping("sale-history")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Page<SaleHistoryResponse>> getSaleHistories(
        @PageableDefault(page = 1) Pageable pageable
    ) {
        Long memberId = securityUtil.getCurrentMemberId();
        Page<SaleHistoryResponse> response =
            historyService.getUsersSaleHistory(pageable, memberId);
        return ResponseDTO.res(response, "조회에 성공하였습니다.");
    }
}
