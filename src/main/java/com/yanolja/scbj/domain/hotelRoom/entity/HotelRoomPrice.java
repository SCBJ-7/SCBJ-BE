package com.yanolja.scbj.domain.hotelRoom.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HotelRoomPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("호텔 객실 가격 식별자")
    private Long id;

    @OneToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @Comment("호텔 객실 식별자")
    private Hotel hotel;

    @Column(nullable = false)
    @Comment("가격(성수기_정가)")
    private int peakPrice;

    @Column(nullable = false)
    @Comment("가격(비 성수기_정가)")
    private int offPeakPrice;

    @Builder
    private HotelRoomPrice(Long id, Hotel hotel, int peakPrice, int offPeakPrice) {
        this.id = id;
        this.hotel = hotel;
        this.peakPrice = peakPrice;
        this.offPeakPrice = offPeakPrice;
    }
}
