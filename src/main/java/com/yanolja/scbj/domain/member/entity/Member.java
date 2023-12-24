package com.yanolja.scbj.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Entity
@EnableJpaAuditing
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("사용자 식별자")
    private Long id;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    @Comment("사용자 이메일")
    private String email;

    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    @Comment("사용자 비밀번호")
    private String password;

    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    @Comment("사용자 이름")
    private String name;

    @Column(columnDefinition = "VARCHAR(50)", nullable = false)
    @Comment("사용자 전화번호")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("사용자 권한")
    private Authority authority;

    @CreatedDate
    @Comment("사용자 정보 생성일")
    private LocalDateTime createdAt;
    @Column(insertable = false)
    @LastModifiedDate
    @Comment("사용자 정보 업데이트일")
    private LocalDateTime updatedAt;

    @Builder
    public Member(Long id, String email, String password, String name, String phone,
        Authority authority,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.authority = authority;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }


}
