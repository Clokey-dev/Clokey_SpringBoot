package com.clokey.server.domain.history.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.history.exception.annotation.HistoryDateFormat;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class HistoryDateFormatValidator implements ConstraintValidator<HistoryDateFormat, String> {

    //YYYY-MM-dd에 대한 정규 표현식.
    private static final String DATE_PATTERN = "\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])";

    @Override
    public void initialize(HistoryDateFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        //null이나 빈값이 아니며 YYYY-MM 형태를 만족해야 한다.
        boolean isValid = value != null && !value.isEmpty() && value.matches(DATE_PATTERN);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.DATE_INVALID.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
