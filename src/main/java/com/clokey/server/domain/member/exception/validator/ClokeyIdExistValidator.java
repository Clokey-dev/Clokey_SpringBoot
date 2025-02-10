package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.exception.annotation.ClokeyIdExist;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClokeyIdExistValidator implements ConstraintValidator<ClokeyIdExist, String>{

    private final MemberRepositoryService memberRepositoryService;

    @Override
    public void initialize(ClokeyIdExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        boolean exists=memberRepositoryService.idExist(s);

        if(s==null || exists){
            return true;
        }
        else{
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorStatus.CLOKEY_ID_INVALID.toString()).addConstraintViolation();
            return false;
        }
    }
}

