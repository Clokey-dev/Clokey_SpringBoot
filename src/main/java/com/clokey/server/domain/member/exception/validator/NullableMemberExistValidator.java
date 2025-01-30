package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.exception.annotation.MemberExist;
import com.clokey.server.domain.member.exception.annotation.NullableMemberExist;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NullableMemberExistValidator implements ConstraintValidator<NullableMemberExist, Long> {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    public void initialize(NullableMemberExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long memberId, ConstraintValidatorContext context) {
        boolean isValid = memberId == null || memberRepositoryService.memberExist(memberId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_MEMBER.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
