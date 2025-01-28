package com.clokey.server.domain.cloth.exception.annotation;

import com.clokey.server.domain.cloth.exception.validator.ClothImageFormatValidator;
import com.clokey.server.domain.cloth.exception.validator.ClothImagePresenceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ClothImageFormatValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ClothImageFormat {
    String message() default "옷 이미지 형식이 올바르지 않았습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
