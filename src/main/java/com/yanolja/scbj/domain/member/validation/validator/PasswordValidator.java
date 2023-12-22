package com.yanolja.scbj.domain.member.validation.validator;

import com.yanolja.scbj.domain.member.validation.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    private String PASSWORD_REGEX;

    @Override
    public void initialize(Password constraintAnnotation) {
        this.PASSWORD_REGEX = constraintAnnotation.regexp();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        System.out.println("비밀번호 검증 중: " + value);
        return Pattern.matches(PASSWORD_REGEX, value);
    }
}
