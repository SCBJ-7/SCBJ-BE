package com.yanolja.scbj.domain.member.validation.validator;

import com.yanolja.scbj.domain.member.validation.AccessToken;
import com.yanolja.scbj.global.config.jwt.JwtUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TokenValidator implements ConstraintValidator<AccessToken, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value !=null && value.startsWith(JwtUtil.GRANT_TYPE);
    }
}
