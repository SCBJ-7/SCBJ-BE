package com.yanolja.scbj.domain.paymentHistory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("결제 내역 식별자")
    private Long id;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("만 14세 이상 이용 동의")
    private boolean isAgeOver14;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("이용 규칙 동의")
    private boolean useAgree;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("취소 및 환불 규칙 동의")
    private boolean cancelAndRefund;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("개인정보 수집 및 이용 동의")
    private boolean collectPersonalInfo;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("개인정보 제 3자 제공 동의")
    private boolean thirdPartySharing;


    @Builder
    private PaymentAgreement(Long id, boolean isAgeOver14, boolean useAgree,
        boolean cancelAndRefund,
        boolean collectPersonalInfo, boolean thirdPartySharing) {
        this.id = id;
        this.isAgeOver14 = isAgeOver14;
        this.useAgree = useAgree;
        this.cancelAndRefund = cancelAndRefund;
        this.collectPersonalInfo = collectPersonalInfo;
        this.thirdPartySharing = thirdPartySharing;
    }
}
