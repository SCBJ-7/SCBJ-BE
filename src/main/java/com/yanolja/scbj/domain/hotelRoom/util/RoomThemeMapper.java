package com.yanolja.scbj.domain.hotelRoom.util;

import com.yanolja.scbj.domain.hotelRoom.dto.response.RoomThemeFindResponse;
import com.yanolja.scbj.domain.hotelRoom.entity.RoomTheme;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoomThemeMapper {

    public static RoomThemeFindResponse toFindResponse(RoomTheme roomTheme){
        return RoomThemeFindResponse.builder()
            .parkingZone(roomTheme.hasParkingZone())
            .breakfast(roomTheme.hasBreakfast())
            .pool(roomTheme.hasPool())
            .oceanView(roomTheme.hasOceanView())
            .build();
    }

}
