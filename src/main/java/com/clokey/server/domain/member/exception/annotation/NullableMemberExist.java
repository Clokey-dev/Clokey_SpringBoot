package com.clokey.server.domain.member.exception.annotation;

import com.clokey.server.domain.member.exception.validator.MemberExistValidator;
import com.clokey.server.domain.member.exception.validator.NullableMemberExistValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NullableMemberExistValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface NullableMemberExist {

    String message() default "해당하는 멤버가 ID가 존재하지 않거나 Null 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
