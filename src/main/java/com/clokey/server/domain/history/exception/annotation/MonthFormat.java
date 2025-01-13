package com.clokey.server.domain.history.exception.annotation;


import com.clokey.server.domain.history.exception.validator.HistoryExistValidator;
import com.clokey.server.domain.history.exception.validator.MonthFormatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MonthFormatValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MonthFormat {

    String message() default "월의 입력형식이 올바르지 않습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
