package com.clokey.server.domain.term.exception.annotation;

import com.clokey.server.domain.term.exception.validator.EssentialTermAgreeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Constraint(validatedBy = EssentialTermAgreeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EssentialTermAgree {
    String message() default "필수 약관에 동의하지 않았습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

