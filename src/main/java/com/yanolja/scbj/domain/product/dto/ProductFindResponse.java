package com.yanolja.scbj.domain.product.dto;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
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

    private List<RoomThemeFindResponse> roomThemeList = new ArrayList<>();
    private String hotelAddress;
    private boolean saleStatus;

    @Builder
    private ProductFindResponse(String hotelName, String roomName, LocalDateTime checkIn,
        LocalDateTime checkOut, int originalPrice, int sellingPrice, int standardPeople,
        int maxPeople,
        String bedType, List<RoomThemeFindResponse> roomThemeList, String hotelAddress,
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
        this.roomThemeList = roomThemeList;
        this.hotelAddress = hotelAddress;
        this.saleStatus = saleStatus;
    }
}
