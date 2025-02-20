package com.clokey.server.domain.member.exception.annotation;

import com.clokey.server.domain.member.exception.validator.IdExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdExistValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdExist {

    String message() default "해당 클로키 아이디가 이미 존재합니다.";

    Class<?>[] groups() default {};  // 그룹

    Class<? extends Payload>[] payload() default {};
}
