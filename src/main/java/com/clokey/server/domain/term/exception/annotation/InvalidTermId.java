package com.clokey.server.domain.term.exception.annotation;

import com.clokey.server.domain.term.exception.validator.EssentialTermAgreeValidator;
import com.clokey.server.domain.term.exception.validator.InvalidTermIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = InvalidTermIdValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER ,ElementType.TYPE}) // 메소드, 필드, 파라미터에서 사용 가능
@Retention(RetentionPolicy.RUNTIME)
public @interface InvalidTermId {
    String message() default "잘못된 약관 ID입니다.";  // 기본 에러 메시지

    Class<?>[] groups() default {};  // 그룹

    Class<? extends Payload>[] payload() default {};  // 페이로드
}
