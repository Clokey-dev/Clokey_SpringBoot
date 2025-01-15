package com.clokey.server.domain.member.exception.annotation;

import com.clokey.server.domain.member.exception.validator.IdExistValidator;
import com.clokey.server.domain.member.exception.validator.IdValidValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IdValidValidator.class)  // 유효성 검사 로직을 IdExistValidator 클래스에서 처리
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER }) // 메소드, 필드, 파라미터에서 사용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface IdValid {

    String message() default "잘못된 클로키 아이디입니다.";  // 기본 에러 메시지

    Class<?>[] groups() default {};  // 그룹

    Class<? extends Payload>[] payload() default {};  // 페이로드
}