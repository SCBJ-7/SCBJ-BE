package com.yanolja.scbj.domain.like.entity;

import com.yanolja.scbj.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "favorite",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "product_id"})
    }
)
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("찜 식별자")
    private Long id;

    @Column
    @Comment("멤버 식별자")
    private Long memberId;

    @Column
    @Comment("상품 식별자")
    private Long productId;


    @Column(nullable = false)
    @Comment("찜 상태")
    private boolean favoriteStatement;


    @Builder
    private Favorite(Long id, Long memberId, Long productId, boolean favoriteStatement) {
        this.id = id;
        this.memberId = memberId;
        this.productId = productId;
        this.favoriteStatement = favoriteStatement;
    }
}

