package com.yanolja.scbj.domain.paymentHistory.util;

import com.yanolja.scbj.domain.paymentHistory.dto.redis.PaymentRedisDto;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentAgreement;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentAgreementMapper {

    public static PaymentAgreement toPaymentAgreement(PaymentRedisDto paymentRedisResponse) {
        return PaymentAgreement.builder()
            .isAgeOver14(paymentRedisResponse.isAgeOver14())
            .useAgree(paymentRedisResponse.useAgree())
            .cancelAndRefund(paymentRedisResponse.cancelAndRefund())
            .collectPersonalInfo(paymentRedisResponse.collectPersonalInfo())
            .thirdPartySharing(paymentRedisResponse.thirdPartySharing())
            .build();
    }
}
