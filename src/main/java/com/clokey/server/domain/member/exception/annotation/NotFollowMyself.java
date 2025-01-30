package com.clokey.server.domain.member.exception.annotation;

import com.clokey.server.domain.member.exception.validator.NotFollowMyselfValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Documented
@Constraint(validatedBy = NotFollowMyselfValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER , ElementType.TYPE}) // 메소드, 필드, 파라미터에서 사용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface NotFollowMyself {
    String message() default "myClokeyId와 yourClokeyId는 동일할 수 없습니다."; // 기본 오류 메시지

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
