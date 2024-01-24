package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PreparePaymentResponse;

public interface PaymentApiService {

    PreparePaymentResponse preparePayment(Long productId, PaymentReadyRequest paymentReadyRequest);

    void approvePaymentWithLock(String pgToken);

    void cancelPayment();
}