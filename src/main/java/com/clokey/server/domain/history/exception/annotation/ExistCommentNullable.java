package com.clokey.server.domain.history.exception.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = com.clokey.server.domain.history.exception.validator.ExistCommentNullableValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistCommentNullable {

    String message() default "대댓글 대상 댓글은 입력하지 않거나 입력할 경우 반드시 존재해야합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
