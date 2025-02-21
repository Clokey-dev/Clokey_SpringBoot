package com.clokey.server.domain.history.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.history.exception.validator.ParentCommentsConditionsValidator;

@Documented
@Constraint(validatedBy = ParentCommentsConditionsValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParentCommentConditions {

    String message() default "이미 대댓글인 댓글에 답장할 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
