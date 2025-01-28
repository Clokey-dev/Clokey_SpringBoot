package com.clokey.server.domain.history.exception.annotation;

import com.clokey.server.domain.history.exception.validator.UniqueClothesValidator;
import com.clokey.server.domain.history.exception.validator.UniqueHashtagsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueHashtagsValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueHashtags {

    String message() default "중복된 해시태그가 존재합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
