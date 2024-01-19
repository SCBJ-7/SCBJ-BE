package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;

public interface PaymentApiService {

    String preparePayment(Long memberId, Long productId, PaymentReadyRequest paymentReadyRequest);

    void approvePaymentWithLock(String pgToken, Long memberId);

    void cancelPayment(Long memberId);
}