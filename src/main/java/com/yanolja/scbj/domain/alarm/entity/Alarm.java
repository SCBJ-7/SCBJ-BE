package com.yanolja.scbj.domain.alarm.entity;

import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.global.common.BaseEntity;
import com.yanolja.scbj.domain.paymentHistory.entity.PaymentHistory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Alarm extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("알림 식별자")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("유저 식별자")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "payment_history_id", nullable = false)
    @Comment("결제 내역 식별자")
    private PaymentHistory paymentHistory;

    @Column(nullable = false, length = 100)
    @Comment("제목")
    private String title;

    @Column(nullable = false, length = 200)
    @Comment("내용")
    private String content;


    @Builder
    private Alarm(Long id, Member member, PaymentHistory paymentHistory, String title, String content) {
        this.id = id;
        this.member = member;
        this.paymentHistory = paymentHistory;
        this.title = title;
        this.content = content;
    }
}