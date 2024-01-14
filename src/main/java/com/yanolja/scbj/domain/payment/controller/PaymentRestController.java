package com.yanolja.scbj.domain.payment.controller;

import com.yanolja.scbj.domain.payment.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.payment.dto.response.PaymentCancelResponse;
import com.yanolja.scbj.domain.payment.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.payment.service.PaymentService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import jakarta.validation.Valid;
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

    @GetMapping("/{product_id}/payments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<PaymentPageFindResponse> findPaymentPage(@PathVariable("product_id") Long productId){
        return ResponseDTO.res(paymentService.getPaymentPage(productId), "결제 페이지 조회에 성공했습니다.");
    }

    @PostMapping("/{product_id}/payments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Void> reqeustPayments(@PathVariable("product_id") long productId,
        @Valid @RequestBody PaymentReadyRequest paymentReadyRequest) {
        String url = paymentService.kakaoPayReady(securityUtil.getCurrentMemberId(), productId, paymentReadyRequest);
        System.out.println(url);
        return ResponseDTO.res("결제에 성공했습니다.");
    }

    @GetMapping("/kakaopay-success")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Void> successPayments(@RequestParam("pg_token") String pgToken,
        @RequestParam("memberId") Long memberId) {
        paymentService.KakaoPayInfo(pgToken, memberId);
        return ResponseDTO.res("결제에 성공했습니다.");
    }

    @GetMapping("/kakaopay-cancel")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<PaymentCancelResponse> cancelPayments(@RequestParam("memberId") Long memberId) {
        paymentService.kakaoPayCancel(memberId);
        return ResponseDTO.res("결제에 실패했습니다.");
    }

}
