package com.yanolja.scbj.domain.payment.controller;

import com.yanolja.scbj.domain.payment.dto.response.PaymentApproveResponse;
import com.yanolja.scbj.domain.payment.service.PaymentService;
import com.yanolja.scbj.global.common.ResponseDTO;
import com.yanolja.scbj.global.util.SecurityUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
//@RequestMapping("")
public class PaymentRestController {

    private final PaymentService paymentService;
    private final SecurityUtil securityUtil;


    @PostMapping("/v1/products/{product_id}/payments")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO<Object> reqeustPayments(@PathVariable("product_id") long productId) {
        String url = paymentService.kakaoPayReady(securityUtil.getCurrentMemberId(), productId);
        System.out.println(url);
        return ResponseDTO.res("결제에 성공했습니다.");
    }

    @GetMapping("/kakaopay-success")
    @ResponseStatus(HttpStatus.OK)
    public PaymentApproveResponse good(@RequestParam("pg_token") String pgToken,
        @RequestParam("memberId") Long memberId, @RequestParam("orderId") Long orderId) {
        System.out.println(memberId);
        System.out.println(orderId);
        PaymentApproveResponse paymentApproveResponse = paymentService.KakaoPayInfo(pgToken,
            memberId, orderId);
        return paymentApproveResponse;
    }

}
