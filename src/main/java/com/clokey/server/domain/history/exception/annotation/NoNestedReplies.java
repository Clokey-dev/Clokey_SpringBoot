package com.clokey.server.domain.history.exception.annotation;

import com.clokey.server.domain.history.exception.validator.ContentLengthValidator;
import com.clokey.server.domain.history.exception.validator.NoNestedRepliesValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoNestedRepliesValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NoNestedReplies {

    String message() default "이미 대댓글인 댓글에 답장할 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
