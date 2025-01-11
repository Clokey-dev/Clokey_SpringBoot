package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.exception.annotation.MonthFormat;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonthFormatValidator implements ConstraintValidator<MonthFormat, String> {

    //YYYY-MM에 대한 정규 표현식.
    private static final String MONTH_PATTERN = "\\d{4}-(0[1-9]|1[0-2])";

    @Override
    public void initialize(MonthFormat constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        //null이나 빈값이 아니며 YYYY-MM 형태를 만족해야 한다.
        boolean isValid = value!=null && !value.isEmpty() && value.matches(MONTH_PATTERN);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_HISTORY.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
