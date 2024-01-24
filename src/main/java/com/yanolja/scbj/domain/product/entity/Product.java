package com.yanolja.scbj.domain.product.entity;

import com.querydsl.core.annotations.QueryInit;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.member.entity.MemberAgreement;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import com.yanolja.scbj.global.common.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("상품 식별자")
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    @Comment("예약 식별자")
    @QueryInit("hotel.room.checkIn")
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("멤버 식별자")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "product_agreement_id")
    @Comment("상품 약관 식별자")
    private ProductAgreement productAgreement;

    @Column(nullable = false)
    @Comment("1차 양도 가격")
    private int firstPrice;

    @Column
    @Comment("2차 양도 가격")
    private int secondPrice;

    @Column(length = 50, nullable = false)
    @Comment("정산 은행")
    private String bank;

    @Column(length = 100, nullable = false)
    @Comment("정산 계좌")
    private String accountNumber;

    @Column
    @Comment("2차 양도시점")
    private int secondGrantPeriod;

    @OneToOne(mappedBy = "product")
    private PaymentHistory paymentHistory;

    @Column
    @Comment("재고")
    private int stock;

    @Builder
    private Product(Long id, Reservation reservation,
        Member member, ProductAgreement productAgreement, int firstPrice, int secondPrice,
        String bank, String accountNumber, int secondGrantPeriod,
        PaymentHistory paymentHistory) {
        this.id = id;
        this.reservation = reservation;
        this.member = member;
        this.productAgreement = productAgreement;
        this.firstPrice = firstPrice;
        this.secondPrice = secondPrice;
        this.bank = bank;
        this.accountNumber = accountNumber;
        this.secondGrantPeriod = secondGrantPeriod;
        this.paymentHistory = paymentHistory;
        this.stock = 1;
    }

    public void delete(LocalDateTime deleteTime){
        super.delete(deleteTime);
    }

    public void sell(){
        this.stock--;
    }

}