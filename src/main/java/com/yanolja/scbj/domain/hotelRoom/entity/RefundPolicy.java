package com.yanolja.scbj.domain.hotelRoom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RefundPolicy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment(value = "호텔 룸 식별자")
    @ManyToOne
    @JoinColumn(name = "hotel_room_id", nullable = false)
    private HotelRoom hotelRoom;

    @Comment(value = "기준 날짜")
    @Column(nullable = false)
    private LocalDate baseDate;

    @Comment(value = "퍼센트")
    @Column(nullable = false)
    private int percent;

    @Builder
    private RefundPolicy(HotelRoom hotelRoom, LocalDate baseDate, int percent) {
        this.hotelRoom = hotelRoom;
        this.baseDate = baseDate;
        this.percent = percent;
    }
}
