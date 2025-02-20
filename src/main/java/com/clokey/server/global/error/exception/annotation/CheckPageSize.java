package com.clokey.server.global.error.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.global.error.exception.validator.CheckPageSizeValidator;

@Documented
@Constraint(validatedBy = CheckPageSizeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckPageSize {

    String message() default "페이지 사이즈가 1보다 작을 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
