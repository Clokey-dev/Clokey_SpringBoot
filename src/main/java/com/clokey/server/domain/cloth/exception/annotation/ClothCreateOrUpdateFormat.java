package com.clokey.server.domain.cloth.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.cloth.exception.validator.ClothCreateOrUpdateFormatValidator;

@Documented
@Constraint(validatedBy = ClothCreateOrUpdateFormatValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ClothCreateOrUpdateFormat {

    String message() default "생성/수정 포맷이 올바르지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
