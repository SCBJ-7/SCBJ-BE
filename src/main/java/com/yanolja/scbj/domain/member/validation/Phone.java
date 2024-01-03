package com.yanolja.scbj.domain.member.validation;

import com.yanolja.scbj.domain.member.validation.validator.PhoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PhoneValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Phone {

    String message() default "입력된 핸드폰 번호 형식이 맞지 않습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String regexp() default "^01([0|1|6|7|8|9])-?([0-9]{4})-?([0-9]{4})$";

}

