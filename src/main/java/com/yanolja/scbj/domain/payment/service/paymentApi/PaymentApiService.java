package com.yanolja.scbj.domain.payment.service.paymentApi;

import com.yanolja.scbj.domain.payment.dto.request.PaymentReadyRequest;

public interface PaymentApiService {

    String payReady(Long memberId, Long productId, PaymentReadyRequest paymentReadyRequest);

    void payInfo(String pgToken, Long memberId);

    void payCancel(Long memberId);
}
