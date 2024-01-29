package com.yanolja.scbj.domain.reservation.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.yanolja.scbj.domain.hotelRoom.entity.Hotel;
import com.yanolja.scbj.domain.member.entity.YanoljaMember;
import com.yanolja.scbj.global.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("예약 식별자")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @Comment("호텔 룸 식별자")
    private Hotel hotel;

    @ManyToOne
    @JoinColumn(name = "yanolja_member_id", nullable = false)
    @Comment("야놀자 회원 식별자")
    private YanoljaMember yanoljaMember;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    @Comment("시작일")
    private LocalDateTime startDate;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Column(nullable = false)
    @Comment("종료일")
    private LocalDateTime endDate;

    @Column(nullable = false)
    @Comment("구매가")
    private int purchasePrice;

    @Builder
    private Reservation(Long id, Hotel hotel, YanoljaMember yanoljaMember, LocalDateTime startDate,
        LocalDateTime endDate, int purchasePrice) {
        this.id = id;
        this.hotel = hotel;
        this.yanoljaMember = yanoljaMember;
        this.startDate = startDate;
        this.endDate = endDate;
        this.purchasePrice = purchasePrice;
    }
}
