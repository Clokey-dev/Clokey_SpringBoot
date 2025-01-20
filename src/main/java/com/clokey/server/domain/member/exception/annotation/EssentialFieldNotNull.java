package com.clokey.server.domain.member.exception.annotation;

import com.clokey.server.domain.member.exception.validator.EssentialFieldNotNullValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Constraint(validatedBy = EssentialFieldNotNullValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface EssentialFieldNotNull {
    String message() default "필수 요소가 입력되지 않았습니다."; // 기본 메시지

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


/*
package com.clokey.server.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NotBlankWithCodeValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotBlankWithCode {
    String message() default "필수 입력 항목입니다."; // 기본 메시지

    String errorCode() default "COMMON400"; // 디폴트 에러 코드

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

 */