package com.clokey.server.domain.history.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.history.exception.validator.UniqueClothesValidator;

@Documented
@Constraint(validatedBy = UniqueClothesValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueClothes {

    String message() default "중복된 옷이 존재합니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
