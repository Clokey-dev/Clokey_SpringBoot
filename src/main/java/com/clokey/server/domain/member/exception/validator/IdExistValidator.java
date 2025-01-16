package com.clokey.server.domain.member.exception.validator;

import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.exception.annotation.IdExist;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IdExistValidator implements ConstraintValidator<IdExist, String> {

    private final MemberRepositoryService memberRepositoryService;

    @Override
    public void initialize(IdExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 값이 null 또는 비어있는 경우 유효성 검사 통과
        if (value == null || value.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ESSENTIAL_INPUT_REQUIRED.toString()).addConstraintViolation();
            return false;
        }

        // 아이디가 이미 존재하는지 확인
        boolean exists = memberRepositoryService.idExist(value);

        if (exists) {
            // 기본 메시지 비활성화 및 커스텀 메시지 설정
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.DUPLICATE_CLOKEY_ID.toString()).addConstraintViolation();
            return false; // 유효하지 않음
        }

        return true; // 유효
    }
}
