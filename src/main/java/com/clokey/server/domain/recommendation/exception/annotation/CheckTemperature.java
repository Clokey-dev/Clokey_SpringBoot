package com.clokey.server.domain.recommendation.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.recommendation.exception.validator.CheckTemperatureValidator;

@Documented
@Constraint(validatedBy = CheckTemperatureValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckTemperature {

    String message() default "온도 범위를 벗어났습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
