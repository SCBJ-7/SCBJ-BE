package com.yanolja.scbj.domain.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("결제 내역 식별자")
    private Long id;


    @Builder
    public PaymentAgreement(Long id) {
        this.id = id;
    }
}
