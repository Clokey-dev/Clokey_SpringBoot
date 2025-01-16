package com.clokey.server.domain.term.exception.validator;

import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EssentialTermAgreeValidator implements ConstraintValidator<EssentialTermAgree, List<TermRequestDTO.Join.Term>> {
    @Override
    public void initialize(EssentialTermAgree constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<TermRequestDTO.Join.Term> terms, ConstraintValidatorContext context) {
        if (terms == null || terms.isEmpty()) {
            return false; // 약관 리스트가 비어있으면 유효하지 않음
        }

        // 필수 약관이 동의되지 않았는지 확인
        boolean allEssentialAgreed = terms.stream()
                .filter(term -> term.getAgreed() != null) // 동의 여부가 null이 아닌 경우에만 체크
                .allMatch(term -> term.getAgreed() || term.getTermId() == null); // 필수 약관 조건
        if(!allEssentialAgreed) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.DUPLICATE_CLOKEY_ID.toString()).addConstraintViolation();
        }
        return allEssentialAgreed;
    }
}

