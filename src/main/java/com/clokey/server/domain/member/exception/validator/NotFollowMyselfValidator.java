package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.member.exception.annotation.NotFollowMyself;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotFollowMyselfValidator implements ConstraintValidator<NotFollowMyself, MemberDTO.FollowRQ> {
    @Override
    public void initialize(NotFollowMyself constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MemberDTO.FollowRQ followRQ, ConstraintValidatorContext constraintValidatorContext) {
        if (followRQ == null ) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorStatus.ESSENTIAL_INPUT_REQUIRED.toString()).addConstraintViolation();
            return false;
        }

        String myClokeyId = followRQ.getMyClokeyId();
        String yourClokeyId = followRQ.getYourClokeyId();

        if(myClokeyId.equals(yourClokeyId)){
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(ErrorStatus.CANNOT_FOLLOW_MYSELF.toString()).addConstraintViolation();
            return false;
        }
        return true;
    }
}
