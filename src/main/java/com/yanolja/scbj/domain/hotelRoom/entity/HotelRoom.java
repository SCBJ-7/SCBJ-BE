package com.yanolja.scbj.domain.hotelRoom.entity;

import com.yanolja.scbj.domain.reservation.entity.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HotelRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("호텔 객실 식별자")
    private Long id;

    @OneToOne
    @JoinColumn(name = "room_theme_id", nullable = false)
    @Comment("객실 테마")
    private RoomTheme roomTheme;

    @OneToMany(mappedBy = "hotelRoom")
    private List<Reservation> reservationList = new ArrayList<>();

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

    @Column(length = 50, nullable = false)
    @Comment("호텔 이름")
    private String hotelName;

    @Column(length = 50, nullable = false)
    @Comment("호텔 주소 대분류")
    private String hotelMainAddress;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("호텔 상세 주소")
    private String hotelDetailAddress;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("호텔 정보 url")
    private String hotelInfoUrl;

    @Builder
    private HotelRoom(RoomTheme roomTheme, String roomName, LocalTime checkIn, LocalTime checkOut,
        String bedType, int standardPeople, int maxPeople, String hotelName,
        String hotelMainAddress,
        String hotelDetailAddress, String hotelInfoUrl) {
        this.roomTheme = roomTheme;
        this.roomName = roomName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.bedType = bedType;
        this.standardPeople = standardPeople;
        this.maxPeople = maxPeople;
        this.hotelName = hotelName;
        this.hotelMainAddress = hotelMainAddress;
        this.hotelDetailAddress = hotelDetailAddress;
        this.hotelInfoUrl = hotelInfoUrl;
    }
}
