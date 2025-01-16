package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.exception.annotation.MemberExist;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberExistValidator implements ConstraintValidator<MemberExist, Long> {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    public void initialize(MemberExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long memberId, ConstraintValidatorContext context) {
        boolean isValid = memberRepositoryService.memberExist(memberId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_MEMBER.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
