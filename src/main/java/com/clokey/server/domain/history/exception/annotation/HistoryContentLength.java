package com.clokey.server.domain.history.exception.annotation;

import com.clokey.server.domain.history.exception.validator.HistoryContentLengthValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HistoryContentLengthValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryContentLength {

    String message() default "기록의 내용은 200자를 넘어가서는 안됍니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
