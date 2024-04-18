package com.yanolja.scbj.domain.product.dto.response;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;


public record ProductFindResponse(
    String hotelName,
    List<String> hotelImageUrlList,
    String roomName,
    LocalDateTime checkIn,
    LocalDateTime checkOut,
    int originalPrice,
    int sellingPrice,
    int standardPeople,
    int maxPeople,
    String bedType,
    RoomThemeFindResponse roomTheme,
    String hotelAddress,
    String hotelInfoUrl,
    boolean saleStatus,
    boolean isSeller,
    String hotelLevel,
    List<String> sellerCommentList,
    String facilityInformation,
    boolean isLike
) {

    @Builder
    public ProductFindResponse {
    }
}