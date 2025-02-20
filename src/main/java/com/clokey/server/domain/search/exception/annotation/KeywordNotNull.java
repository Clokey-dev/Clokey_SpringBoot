package com.clokey.server.domain.search.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.search.exception.validator.KeywordNotNullValidator;

@Documented
@Constraint(validatedBy = KeywordNotNullValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface KeywordNotNull {
    String message() default "검색어는 필수입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
