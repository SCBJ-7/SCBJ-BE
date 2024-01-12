package com.yanolja.scbj.domain.payment.controller;

import com.yanolja.scbj.domain.payment.dto.response.PaymentPageFindResponse;
import com.yanolja.scbj.domain.payment.service.PaymentService;
import com.yanolja.scbj.global.common.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/products/{product_id}/payments")
public class PaymentRestController {

    private final PaymentService paymentService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public ResponseDTO<PaymentPageFindResponse> findPaymentPage(@PathVariable("product_id") Long productId){
        return ResponseDTO.res(paymentService.getPaymentPage(productId), "결제 페이지 조회에 성공했습니다.");
    }
}
