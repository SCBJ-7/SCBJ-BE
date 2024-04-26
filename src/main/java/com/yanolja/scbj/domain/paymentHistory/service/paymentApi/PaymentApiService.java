package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PaymentSuccessResponse;
import com.yanolja.scbj.domain.paymentHistory.dto.response.PreparePaymentResponse;

public interface PaymentApiService {

    PreparePaymentResponse preparePayment(Long productId, PaymentReadyRequest paymentReadyRequest);

    PaymentSuccessResponse approvePaymentWithLock(String pgToken);

    void cancelPayment();

    void refundPayment(Long paymentHistoryId);
}