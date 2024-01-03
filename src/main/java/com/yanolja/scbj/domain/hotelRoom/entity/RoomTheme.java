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
    private boolean parkingZone;

    @Column(nullable = false)
    @Comment("조식 제공")
    private boolean breakfast;

    @Column(nullable = false)
    @Comment("수영장")
    private boolean pool;

    @Column(nullable = false)
    @Comment("오션뷰")
    private boolean oceanView;

    @Builder
    private RoomTheme(Long id, boolean parkingZone, boolean breakfast, boolean pool,
        boolean oceanView) {
        this.id = id;
        this.parkingZone = parkingZone;
        this.breakfast = breakfast;
        this.pool = pool;
        this.oceanView = oceanView;
    }

    public boolean hasParkingZone() {
        return parkingZone;
    }

    public boolean hasBreakfast() {
        return breakfast;
    }

    public boolean hasPool() {
        return pool;
    }

    public boolean hasOceanView() {
        return oceanView;
    }
}
