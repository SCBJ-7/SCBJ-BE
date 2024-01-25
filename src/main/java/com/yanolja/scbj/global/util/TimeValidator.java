package com.yanolja.scbj.global.util;


import com.yanolja.scbj.domain.product.entity.Product;
import com.yanolja.scbj.domain.product.enums.SecondTransferExistence;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeValidator {

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

    public boolean isOverSecondGrantPeriod(Product product, LocalDateTime checkInDateTime){
        LocalDateTime changeTime = null;
        if(product.getSecondGrantPeriod() != SecondTransferExistence.NOT_EXISTS.getStatus()){

            long changeHour = product.getSecondGrantPeriod();
            changeTime = checkInDateTime.minusHours(changeHour);

            if (changeTime.isBefore(LocalDateTime.now())) {
                return true;
            }
        }
        return false;
    }

}
