package com.clokey.server.domain.cloth.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.cloth.exception.validator.ClothImagePresenceValidator;

@Documented
@Constraint(validatedBy = ClothImagePresenceValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ClothImagePresence {

    String message() default "옷 이미지가 입력되지 않았습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
