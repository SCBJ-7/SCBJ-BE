package com.yanolja.scbj.domain.paymentHistory.dto.request;


public record PaymentReadyRequest(
    String customerName,
    String customerEmail,
    String customerPhoneNumber
) {

}
