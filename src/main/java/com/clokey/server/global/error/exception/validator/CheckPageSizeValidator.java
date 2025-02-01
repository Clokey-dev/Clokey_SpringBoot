package com.clokey.server.global.error.exception.validator;

import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.annotation.CheckPage;
import com.clokey.server.global.error.exception.annotation.CheckPageSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckPageSizeValidator implements ConstraintValidator<CheckPageSize, Integer> {

    @Override
    public void initialize(CheckPageSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        boolean isValid = value > 0;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.PAGE_SIZE_UNDER_ONE.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
