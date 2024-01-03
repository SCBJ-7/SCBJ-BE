package com.yanolja.scbj.domain.hotelRoom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomTheme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("객실 테마 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("주차 가능")
    private boolean hasParkingZone;

    @Column(nullable = false)
    @Comment("조식 제공")
    private boolean hasBreakfast;

    @Column(nullable = false)
    @Comment("수영장")
    private boolean hasPool;

    @Column(nullable = false)
    @Comment("오션뷰")
    private boolean hasOceanView;

    @Builder
    private RoomTheme(Long id, boolean hasParkingZone, boolean hasBreakfast, boolean hasPool,
        boolean hasOceanView) {
        this.id = id;
        this.hasParkingZone = hasParkingZone;
        this.hasBreakfast = hasBreakfast;
        this.hasPool = hasPool;
        this.hasOceanView = hasOceanView;
    }
}
