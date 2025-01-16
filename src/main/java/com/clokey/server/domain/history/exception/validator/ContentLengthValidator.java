package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.exception.annotation.CheckPage;
import com.clokey.server.domain.history.exception.annotation.ContentLength;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentLengthValidator implements ConstraintValidator<ContentLength, String> {

    @Override
    public void initialize(ContentLength constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String content, ConstraintValidatorContext context) {
        boolean isValid = content.length() <= 50;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.COMMENT_LENGTH_OUT_OF_RANGE.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
