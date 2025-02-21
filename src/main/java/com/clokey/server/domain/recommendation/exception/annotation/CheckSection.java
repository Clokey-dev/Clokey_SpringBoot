package com.clokey.server.domain.recommendation.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.recommendation.exception.validator.CheckSectionValidator;

@Documented
@Constraint(validatedBy = CheckSectionValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckSection {

    String message() default "해당 섹션이 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
