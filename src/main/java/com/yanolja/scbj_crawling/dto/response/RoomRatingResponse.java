package com.yanolja.scbj_crawling.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomRatingResponse {
    private String roomFacilityInfo;
    private String roomAllRating;
    private String roomKindnessRating;
    private String roomCleanlinessRating;
    private String roomConvenienceRating;
    private String roomLocationRating;

    @Builder
    public RoomRatingResponse(String roomFacilityInfo, String roomAllRating,
        String roomKindnessRating,
        String roomCleanlinessRating, String roomConvenienceRating, String roomLocationRating) {
        this.roomFacilityInfo = roomFacilityInfo;
        this.roomAllRating = roomAllRating;
        this.roomKindnessRating = roomKindnessRating;
        this.roomCleanlinessRating = roomCleanlinessRating;
        this.roomConvenienceRating = roomConvenienceRating;
        this.roomLocationRating = roomLocationRating;
    }
}
