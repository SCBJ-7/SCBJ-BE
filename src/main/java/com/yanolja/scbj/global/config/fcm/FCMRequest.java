package com.yanolja.scbj.global.config.fcm;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FCMRequest {

    @JsonProperty("validate_only")
    private final boolean validateOnly;
    private final Message message;

    @RequiredArgsConstructor
    @Getter
    public static class Message {
        private final Data data;
        private final String token;
    }

    @RequiredArgsConstructor
    @Getter
    public static class Data {

        private final String title;
        private final String message;
        private final LocalDateTime date;

    }
}
