package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.exception.annotation.HistoryContentLength;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoryContentLengthValidator implements ConstraintValidator<HistoryContentLength, String> {

    @Override
    public void initialize(HistoryContentLength constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String content, ConstraintValidatorContext context) {
        boolean isValid = content.length() <= 200;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.HISTORY_CONTENT_OUT_OF_RANGE.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
