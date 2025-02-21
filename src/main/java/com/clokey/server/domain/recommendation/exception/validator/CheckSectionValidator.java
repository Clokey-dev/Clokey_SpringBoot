package com.clokey.server.domain.recommendation.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.recommendation.exception.annotation.CheckSection;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class CheckSectionValidator implements ConstraintValidator<CheckSection, String> {

    @Override
    public void initialize(CheckSection constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = value.equals("closet") || value.equals("calendar");

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_SECTION.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
