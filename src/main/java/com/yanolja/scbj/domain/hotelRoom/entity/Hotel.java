package com.yanolja.scbj.domain.hotelRoom.entity;

import com.querydsl.core.annotations.QueryInit;
import com.yanolja.scbj.domain.reservation.entity.Reservation;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("호텔 객실 식별자")
    private Long id;

    @OneToMany(mappedBy = "hotel")
    private List<Reservation> reservationList = new ArrayList<>();

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

    @OneToOne(mappedBy = "hotel")
    private HotelRoomPrice hotelRoomPrice;

    @Embedded
    private Room room;

    @OneToMany(mappedBy = "hotel")
    List<HotelRoomImage> hotelRoomImageList = new ArrayList<>();

    @OneToMany(mappedBy = "hotel")
    List<RefundPolicy> hotelRefundPolicyList = new ArrayList<>();

    @Builder
    private Hotel(Long id, List<Reservation> reservationList, String hotelName,
        String hotelMainAddress,
        String hotelDetailAddress, String hotelInfoUrl, HotelRoomPrice hotelRoomPrice, Room room,
        List<HotelRoomImage> hotelRoomImageList, List<RefundPolicy> hotelRefundPolicyList) {
        this.id = id;
        this.reservationList = reservationList;
        this.hotelName = hotelName;
        this.hotelMainAddress = hotelMainAddress;
        this.hotelDetailAddress = hotelDetailAddress;
        this.hotelInfoUrl = hotelInfoUrl;
        this.hotelRoomPrice = hotelRoomPrice;
        this.room = room;
        this.hotelRoomImageList = hotelRoomImageList;
        this.hotelRefundPolicyList = hotelRefundPolicyList;
    }
}
