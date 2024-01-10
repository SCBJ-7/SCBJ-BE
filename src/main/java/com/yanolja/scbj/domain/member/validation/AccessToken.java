package com.yanolja.scbj.domain.member.validation;

import com.yanolja.scbj.domain.member.validation.ValidationGroups.PatternGroup;
import com.yanolja.scbj.domain.member.validation.validator.TokenValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TokenValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessToken {

    String message() default "유효하지 않은 액세스 토큰입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}