package com.yanolja.scbj.domain.product.dto.response;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@NoArgsConstructor
public class ProductFindResponse {

    private String hotelName;
    private String roomName;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private int originalPrice;
    private int sellingPrice;
    private int standardPeople;
    private int maxPeople;
    private String bedType;
    private RoomThemeFindResponse roomTheme;
    private String hotelAddress;
    private String hotelInfoUrl;
    private boolean saleStatus;

    @Builder
    public ProductFindResponse(String hotelName, String roomName, LocalDateTime checkIn,
        LocalDateTime checkOut, int originalPrice, int sellingPrice, int standardPeople,
        int maxPeople,
        String bedType, RoomThemeFindResponse roomTheme, String hotelAddress, String hotelInfoUrl,
        boolean saleStatus) {
        this.hotelName = hotelName;
        this.roomName = roomName;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.originalPrice = originalPrice;
        this.sellingPrice = sellingPrice;
        this.standardPeople = standardPeople;
        this.maxPeople = maxPeople;
        this.bedType = bedType;
        this.roomTheme = roomTheme;
        this.hotelAddress = hotelAddress;
        this.hotelInfoUrl = hotelInfoUrl;
        this.saleStatus = saleStatus;
    }
}