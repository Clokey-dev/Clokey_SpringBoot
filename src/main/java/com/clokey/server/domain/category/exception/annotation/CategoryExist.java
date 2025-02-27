package com.clokey.server.domain.category.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.category.exception.validator.CategoryExistValidator;

@Documented
@Constraint(validatedBy = CategoryExistValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryExist {

    String message() default "해당하는 옷이 존재하지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
