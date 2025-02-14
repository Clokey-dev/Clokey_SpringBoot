package com.clokey.server.domain.recommendation.exception.validator;

import com.clokey.server.domain.recommendation.exception.annotation.CheckSection;
import com.clokey.server.domain.recommendation.exception.annotation.CheckTemperature;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckTemperatureValidator implements ConstraintValidator<CheckTemperature, Integer> {

    @Override
    public void initialize(CheckTemperature constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        boolean isValid = value>=-20 && value<=40;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.OUT_OR_RANGE_TEMP.toString()).addConstraintViolation();
        }

        return isValid;

    }
}


