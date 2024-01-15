package com.yanolja.scbj.domain.paymentHistory.service.paymentApi;

import com.yanolja.scbj.domain.paymentHistory.dto.request.PaymentReadyRequest;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;

public interface PaymentApiService {

    String payReady(Long memberId, Long productId, PaymentReadyRequest paymentReadyRequest);

    void payInfo(String pgToken, Long memberId, Long productId, String tid);

    void payCancel(Long memberId);
}