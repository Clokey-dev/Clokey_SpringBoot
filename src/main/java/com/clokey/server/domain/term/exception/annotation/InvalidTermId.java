package com.clokey.server.domain.term.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.term.exception.validator.InvalidTermIdValidator;

@Documented
@Constraint(validatedBy = InvalidTermIdValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InvalidTermId {
    String message() default "잘못된 약관 ID입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
