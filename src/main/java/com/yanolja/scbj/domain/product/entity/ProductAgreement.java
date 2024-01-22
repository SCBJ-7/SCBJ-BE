package com.yanolja.scbj.domain.product.entity;

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
public class ProductAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("상품 약관 식별자")
    private Long id;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("체크인 기준 판매 자동 완료 방침")
    private Boolean standardTimeSellingPolicy;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("정산 총액 확인 방침")
    private Boolean totalAmountPolicy;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("판매가 수정 불가 방침")
    private Boolean sellingModificationPolicy;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("판매 진행 동의 방침")
    private Boolean productAgreement;

    @Builder
    private ProductAgreement(Long id, Boolean standardTimeSellingPolicy,
        Boolean totalAmountPolicy, Boolean sellingModificationPolicy,
        Boolean productAgreement) {
        this.id = id;
        this.standardTimeSellingPolicy = standardTimeSellingPolicy;
        this.totalAmountPolicy = totalAmountPolicy;
        this.sellingModificationPolicy = sellingModificationPolicy;
        this.productAgreement = productAgreement;
    }
}