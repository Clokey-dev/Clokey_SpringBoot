package com.clokey.server.domain.member.exception.annotation;

import com.clokey.server.domain.history.exception.validator.HistoryExistValidator;
import com.clokey.server.domain.member.exception.validator.MemberExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MemberExistValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface MemberExist {

    String message() default "해당하는 멤버가 ID가 존재하지 않습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

