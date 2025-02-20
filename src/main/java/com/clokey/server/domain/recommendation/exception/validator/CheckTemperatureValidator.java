package com.clokey.server.domain.recommendation.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.recommendation.exception.annotation.CheckTemperature;
import com.clokey.server.global.error.code.status.ErrorStatus;

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
            context.buildConstraintViolationWithTemplate(ErrorStatus.OUT_OF_RANGE_TEMP.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
