package com.yanolja.scbj.domain.member.validation.validator;

import com.yanolja.scbj.domain.member.validation.Phone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneValidator implements ConstraintValidator<Phone, String> {

    private String PHONE_REGEX;

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.PHONE_REGEX = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && Pattern.matches(PHONE_REGEX, value);
    }
}
