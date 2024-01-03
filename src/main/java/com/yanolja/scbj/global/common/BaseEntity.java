package com.yanolja.scbj.global.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate //JPA
    @Comment("생성일")
    private LocalDateTime createdAt;

    @Column(insertable = false)
    @LastModifiedDate
    @Comment("업데이트일")
    private LocalDateTime updatedAt;

    @Column(insertable = false)
    @Comment("삭제일")
    private LocalDateTime deletedAt;

    protected void delete(LocalDateTime currentTime) {
        if (deletedAt == null) {
            deletedAt = currentTime;
        }
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    protected void restore() {
        deletedAt = null;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return this.deletedAt;
    }
}
