package com.clokey.server.domain.member.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.exception.annotation.NullableClokeyIdExist;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class NullableClokeyIdExistValidator implements ConstraintValidator<NullableClokeyIdExist, String> {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    public void initialize(NullableClokeyIdExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String clokeyId, ConstraintValidatorContext context) {
        boolean isValid = clokeyId == null || memberRepositoryService.existsByClokeyId(clokeyId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_MEMBER.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
