package com.yanolja.scbj.domain.payment.dto.request;


public record PaymentReadyRequest(
    String customerName,
    String customerEmail,
    String customerPhoneNumber
) {

}
