package com.yanolja.scbj.domain.paymentHistory.entity;

import com.yanolja.scbj.domain.alarm.dto.CheckInAlarmResponse;
import com.yanolja.scbj.domain.member.entity.Member;
import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SqlResultSetMapping;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Comment;


@NamedNativeQuery(
    name = "find_check_in_alarm",
    query =
        "SELECT ph.id as productHistoryId, m.id as memberId, ph.product_name as productName" +
            " FROM payment_history ph" +
            " INNER JOIN member m on ph.member_id = m.id" +
            " INNER JOIN product p on p.id=ph.product_id" +
            " INNER JOIN reservation r on p.reservation_id = r.id" +
            " WHERE DATE_FORMAT(r.start_date, '%Y-%m-%d %H:%i') =DATE_FORMAT(DATE_SUB(now(),interval 1 DAY),'%Y-%m-%d %H:%i')",
    resultSetMapping="check_in_alarm"
    )

@SqlResultSetMapping(
    name = "check_in_alarm",
    classes = @ConstructorResult(
        targetClass = CheckInAlarmResponse.class,
        columns = {
            @ColumnResult(name = "productHistoryId", type = Long.class),
            @ColumnResult(name = "memberId", type = Long.class),
            @ColumnResult(name = "productName", type = String.class)
        }
    )
)
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PaymentHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("결제 식별자")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    @Comment("유저 식별자")
    private Member member;

    @OneToOne
    @JoinColumn(name = "product_id")
    @Comment("상품 식별자")
    private Product product;

    @Column(length = 50, nullable = false)
    @Comment("상품 이름")
    private String productName;

    @OneToOne
    @JoinColumn(name = "payment_agreement_id")
    @Cascade(CascadeType.ALL)
    @Comment("결제 약관 식별자")
    private PaymentAgreement paymentAgreement;

    @Column(nullable = false)
    @Comment("상품 가격")
    private int price;

    @Column(length = 50, nullable = false)
    @Comment("예약자 성명")
    private String customerName;

    @Column(length = 50, nullable = false)
    @Comment("예약자 이메일")
    private String customerEmail;

    @Column(length = 50, nullable = false)
    @Comment("예약자 휴대폰 번호")
    private String customerPhoneNumber;

    @Column(length = 50, nullable = false)
    @Comment("결제 수단")
    private String paymentType;

    @Column(nullable = false)
    @Comment("정산 상태")
    private boolean settlement;

    @Column(length = 20, nullable = false)
    @Comment("결제 고유 번호")
    private String tid;


    @Builder
    private PaymentHistory(Long id, Member member, Product product, String productName,
        PaymentAgreement paymentAgreement, int price, String customerName, String customerEmail,
        String customerPhoneNumber, String paymentType, boolean settlement, String tid) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.productName = productName;
        this.paymentAgreement = paymentAgreement;
        this.price = price;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhoneNumber = customerPhoneNumber;
        this.paymentType = paymentType;
        this.settlement = settlement;
        this.tid = tid;
    }

    public void processSettlement() {
        if (!this.settlement) {
            this.settlement = true;
        }
    }
}
