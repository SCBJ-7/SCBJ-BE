package com.yanolja.scbj.domain.hotelRoom.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class RoomThemeFindResponse {

    private boolean parkingZone;
    private boolean breakfast;
    private boolean pool;
    private boolean oceanView;

    @Builder
    private RoomThemeFindResponse(boolean parkingZone, boolean breakfast, boolean pool,
        boolean oceanView) {
        this.parkingZone = parkingZone;
        this.breakfast = breakfast;
        this.pool = pool;
        this.oceanView = oceanView;
    }
}

