package com.clokey.server.domain.term.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class EssentialTermAgreeValidator implements ConstraintValidator<EssentialTermAgree, TermRequestDTO.Join> {

    @Override
    public void initialize(EssentialTermAgree constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TermRequestDTO.Join joinRequest, ConstraintValidatorContext context) {
        if (joinRequest == null || joinRequest.getTerms() == null || joinRequest.getTerms().isEmpty()) {
            return false; // 약관 리스트가 비어있으면 유효하지 않음
        }

        // 필수 약관이 동의되지 않았는지 확인
        boolean allEssentialAgreed = joinRequest.getTerms().stream()
                .filter(term -> term.getAgreed() != null) // 동의 여부가 null이 아닌 경우에만 체크
                .allMatch(term -> term.getAgreed() || term.getTermId() == null); // 필수 약관 조건

        if (!allEssentialAgreed) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.ESSENTIAL_TERM_NOT_AGREED.toString()).addConstraintViolation();
        }
        return allEssentialAgreed;
    }
}
