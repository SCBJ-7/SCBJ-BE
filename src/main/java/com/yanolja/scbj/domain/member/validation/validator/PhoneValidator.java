package com.yanolja.scbj.domain.member.validation.validator;

import com.yanolja.scbj.domain.member.validation.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private String PHONE_REGEX ;

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.PHONE_REGEX = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        System.out.println("핸드폰 번호 검증 중: " + value);
        return Pattern.matches(PHONE_REGEX, value);
    }
}
