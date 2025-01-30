package com.clokey.server.domain.cloth.exception.annotation;

import com.clokey.server.domain.cloth.exception.validator.ClothExistValidator;
import com.clokey.server.domain.cloth.exception.validator.ClothImagePresenceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ClothImagePresenceValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ClothImagePresence {

    String message() default "옷 이미지가 입력되지 않았습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
