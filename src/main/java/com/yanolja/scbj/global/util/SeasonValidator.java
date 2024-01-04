package com.yanolja.scbj.global.util;


import java.time.LocalDate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class SeasonValidator {

    private final int JUNE = 6;
    private final int JULY = 7;
    private final int AUGUST = 8;
    private final int DECEMBER = 12;
    private final int JANUARY = 1;
    private final int FEBRUARY = 2;

    public boolean isPeakTime(LocalDate date) {
        int month = date.getMonthValue();
        return month == JUNE || month == JULY || month == AUGUST || month == DECEMBER
            || month == JANUARY || month == FEBRUARY;
    }

}
