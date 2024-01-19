package com.yanolja.scbj.domain.paymentHistory.controller;

import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentCancelResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.paymentHistory.service.PaymentService;
import com.yanolja.scbj.domain.paymentHistory.service.paymentApi.PaymentApiService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/products")
public class PaymentRestController {

    private final PaymentService paymentService;
    private final SecurityUtil securityUtil;
    private final Map<String, PaymentApiService> paymentApiServiceMap;

    @GetMapping("/{product_id}/payments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<PaymentPageFindResponse> findPaymentPage(
        @PathVariable("product_id") Long productId) {
        return ResponseDTO.res(paymentService.getPaymentPage(productId), "결제 페이지 조회에 성공했습니다.");
    }

    @PostMapping("/{product_id}/payments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<String> reqeustPayments(@PathVariable("product_id") long productId,
        @RequestParam("paymentType") String paymentType,
        @Valid @RequestBody PaymentReadyRequest paymentReadyRequest) {

        PaymentApiService paymentApiService = paymentApiServiceMap.get(paymentType);

        String url = paymentApiService.payReady(securityUtil.getCurrentMemberId(), productId,
            paymentReadyRequest);
        System.out.println(url); // Todo: 테스트에서만 필요
        return ResponseDTO.res(url, "결제에 성공했습니다.");
    }

    @GetMapping("/pay-success")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Void> successPayments(@RequestParam("pg_token") String pgToken,
        @RequestParam("memberId") Long memberId, @RequestParam("paymentType") String paymentType) {

        PaymentApiService paymentApiService = paymentApiServiceMap.get(paymentType);

        paymentService
            .stockLock(pgToken, memberId);
        return ResponseDTO.res("결제에 성공했습니다.");
    }

    @GetMapping("/pay-cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<PaymentCancelResponse> cancelPayments(
        @RequestParam("memberId") Long memberId, @RequestParam("paymentType") String paymentType) {

        PaymentApiService paymentApiService = paymentApiServiceMap.get(paymentType);
        paymentApiService.payCancel(memberId);

        return ResponseDTO.res("결제에 실패했습니다.");
    }

    @GetMapping("/test")
    public void test(){
        paymentService.stockLock("test", 1L);
    }


}
