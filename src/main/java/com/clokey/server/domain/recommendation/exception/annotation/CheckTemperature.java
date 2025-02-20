package com.clokey.server.domain.recommendation.exception.annotation;

import com.clokey.server.domain.recommendation.exception.validator.CheckTemperatureValidator;
import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = CheckTemperatureValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckTemperature {

    String message() default "온도 범위를 벗어났습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
