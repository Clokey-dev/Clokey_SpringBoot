package com.clokey.server.domain.history.exception.annotation;

import com.clokey.server.domain.history.exception.validator.CommentContentLengthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CommentContentLengthValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommentContentLength {

    String message() default "댓글의 길이는 50자를 넘어가서는 안됍니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
