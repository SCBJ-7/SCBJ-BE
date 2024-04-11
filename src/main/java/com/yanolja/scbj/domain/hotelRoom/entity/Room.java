package com.yanolja.scbj.domain.hotelRoom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Room {

    @Column(length = 50, nullable = false)
    @Comment("객실 이름")
    private String roomName;

    @Column(nullable = false)
    @Comment("체크인 시간")
    private LocalTime checkIn;

    @Column(nullable = false)
    @Comment("체크아웃 시간")
    private LocalTime checkOut;

    @Column(length = 30, nullable = false)
    @Comment("침대 구성")
    private String bedType;

    @Column(nullable = false)
    @Comment("기준 인원")
    private int standardPeople;

    @Column(nullable = false)
    @Comment("최대 인원")
    private int maxPeople;

    @OneToOne
    @JoinColumn(name = "room_theme_id", nullable = false)
    @Comment("객실 테마")
    private RoomTheme roomTheme;

    @Column(nullable = false)
    @Comment("객실 전체 평점")
    private String roomAllRating;

    @Column(nullable = false)
    @Comment("객실 친절도 평점")
    private String roomKindnessRating;

    @Column(nullable = false)
    @Comment("객실 청결도 평점")
    private String roomCleanlinessRating;

    @Column(nullable = false)
    @Comment("객실 편의성 평점")
    private String roomConvenienceRating;

    @Column(nullable = false)
    @Comment("객실 위치 만족도 평점")
    private String roomLocationRating;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("객실 기본 정보")
    private String facilityInformation;

    @Builder
    private Room(String roomName, LocalTime checkIn, LocalTime checkOut, String bedType,
        int standardPeople, int maxPeople, RoomTheme roomTheme, String roomAllRating,
        String roomKindnessRating, String roomCleanlinessRating, String roomConvenienceRating,
        String roomLocationRating, String facilityInformation) {
        this.roomName = roomName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.bedType = bedType;
        this.standardPeople = standardPeople;
        this.maxPeople = maxPeople;
        this.roomTheme = roomTheme;
        this.roomAllRating = roomAllRating;
        this.roomKindnessRating = roomKindnessRating;
        this.roomCleanlinessRating = roomCleanlinessRating;
        this.roomConvenienceRating = roomConvenienceRating;
        this.roomLocationRating = roomLocationRating;
        this.facilityInformation = facilityInformation;
    }
}
