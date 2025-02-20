package com.clokey.server.domain.member.exception.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.member.exception.validator.EssentialFieldNotNullValidator;


@Constraint(validatedBy = EssentialFieldNotNullValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EssentialFieldNotNull {
    String message() default "필수 요소가 입력되지 않았습니다."; // 기본 메시지

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
