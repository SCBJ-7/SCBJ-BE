package com.yanolja.scbj.domain.hotelRoom.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class RoomThemeFindResponse {
    private boolean hasParkingZone;
    private boolean hasBreakfast;
    private boolean hasPool;
    private boolean hasOceanView;

    @Builder
    private RoomThemeFindResponse(boolean hasParkingZone, boolean hasBreakfast, boolean hasPool,
        boolean hasOceanView) {
        this.hasParkingZone = hasParkingZone;
        this.hasBreakfast = hasBreakfast;
        this.hasPool = hasPool;
        this.hasOceanView = hasOceanView;
    }
}

