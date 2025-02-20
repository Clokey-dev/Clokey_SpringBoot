package com.clokey.server.domain.member.exception.annotation;

import com.clokey.server.domain.member.exception.validator.IdExistValidator;
import com.clokey.server.domain.member.exception.validator.IdValidValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdValidValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdValid {

    String message() default "잘못된 클로키 아이디입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}