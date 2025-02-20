package com.clokey.server.domain.history.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class HistoryExistValidator implements ConstraintValidator<HistoryExist, Long> {

    private final HistoryRepositoryService historyRepositoryService;

    @Override
    public void initialize(HistoryExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long historyId, ConstraintValidatorContext context) {
        boolean isValid = historyRepositoryService.existsById(historyId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_HISTORY.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
