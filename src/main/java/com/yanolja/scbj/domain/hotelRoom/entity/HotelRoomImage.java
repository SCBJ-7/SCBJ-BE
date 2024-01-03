package com.yanolja.scbj.domain.hotelRoom.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class HotelRoomImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("호텔 이미지 식별자")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hotel_id", nullable = false)
    @Comment("호텔 객실 식별자")
    private Hotel hotel;

    @Column(columnDefinition = "TEXT", nullable = false)
    @Comment("이미지 url")
    private String url;

    @Builder
    private HotelRoomImage(Long id, Hotel hotel, String url) {
        this.id = id;
        this.hotel = hotel;
        this.url = url;
    }
}
