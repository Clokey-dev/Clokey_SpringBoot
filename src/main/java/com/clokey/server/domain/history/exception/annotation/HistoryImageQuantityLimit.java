package com.clokey.server.domain.history.exception.annotation;

import java.lang.annotation.*;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.clokey.server.domain.history.exception.validator.HistoryImageQuantityLimitValidator;

@Documented
@Constraint(validatedBy = HistoryImageQuantityLimitValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface HistoryImageQuantityLimit {

    String message() default "기록 사진을 10개 이상 등록할 수 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
