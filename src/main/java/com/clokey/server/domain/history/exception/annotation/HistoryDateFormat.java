package com.clokey.server.domain.history.exception.annotation;

import com.clokey.server.domain.history.exception.validator.HistoryDateFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HistoryDateFormatValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryDateFormat {

    String message() default "기록의 날짜 형식이 올바르지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
