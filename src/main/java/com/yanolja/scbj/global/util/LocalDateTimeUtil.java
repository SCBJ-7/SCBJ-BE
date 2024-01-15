package com.yanolja.scbj.global.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeUtil {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";

    public static String convertToString(LocalDateTime localDateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String formattedDateTime = localDateTime.format(formatter);
        return formattedDateTime;
    }

}
