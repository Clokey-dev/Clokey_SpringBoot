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
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE}) // 메소드, 필드, 파라미터에서 사용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface EssentialTermAgree {
    String message() default "필수 약관에 동의하지 않았습니다.";  // 기본 에러 메시지

    Class<?>[] groups() default {};  // 그룹

    Class<? extends Payload>[] payload() default {};  // 페이로드
}

