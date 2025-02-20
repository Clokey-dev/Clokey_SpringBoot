package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.exception.annotation.IdValid;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class IdValidValidator implements ConstraintValidator<IdValid, String> {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    public void initialize(IdValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if (s == null) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorStatus.ESSENTIAL_INPUT_REQUIRED.toString()).addConstraintViolation();
            return false;
        }

        boolean exists = memberRepositoryService.idExist(s);

        if (exists) {
            return true;
        } else {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorStatus.CLOKEY_ID_INVALID.toString()).addConstraintViolation();
            return false;
        }
    }

}
