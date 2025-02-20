package com.clokey.server.domain.member.exception.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.clokey.server.domain.member.exception.annotation.EssentialFieldNotNull;
import com.clokey.server.global.error.code.status.ErrorStatus;

public class EssentialFieldNotNullValidator implements ConstraintValidator<EssentialFieldNotNull, String> {
    @Override
    public void initialize(EssentialFieldNotNull constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ESSENTIAL_INPUT_REQUIRED.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
