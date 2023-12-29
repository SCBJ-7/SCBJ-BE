package com.yanolja.scbj.domain.payment.entity;

import com.yanolja.scbj.domain.prdouct.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("결제 식별자")
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    @Comment("유저 식별자")
    private Product product;

    @Column(nullable = false)
    @Comment("가격")
    private int price;

    @Column(length = 50, nullable = false)
    @Comment("예약자 성명")
    private String customerName;

    @Column(length = 50, nullable = false)
    @Comment("예약자 이메일")
    private String customerEmail;

    @Column(length = 50, nullable = false)
    @Comment("예약자 전화번호")
    private String customerPhoneNumber;

    @Column(length = 50, nullable = false)
    @Comment("결제 수단")
    private String paymentType;

    @Builder
    private PaymentHistory(Product product, int price, String customerName, String customerEmail,
        String customerPhoneNumber, String paymentType) {
        this.product = product;
        this.price = price;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.paymentType = paymentType;
    }
}
