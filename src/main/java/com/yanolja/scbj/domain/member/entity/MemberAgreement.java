package com.yanolja.scbj.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("회원 약관 식별자")
    private Long id;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("개인정보 수집 및 이용약관")
    private Boolean privacyPolicy;

    @Column(nullable = false)
    @ColumnDefault(value = "false")
    @Comment("개인정보 제3자 제공약관")
    private Boolean isThirdPartyDisclosureAgreed;


}
