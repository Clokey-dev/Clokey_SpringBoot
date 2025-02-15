package com.clokey.server.domain.term.exception.validator;

import com.clokey.server.domain.term.application.TermRepositoryService;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.domain.term.exception.annotation.InvalidTermId;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InvalidTermIdValidator implements ConstraintValidator<InvalidTermId, TermRequestDTO.Join> {

    private final TermRepositoryService termRepositoryService;

    @Override
    public void initialize(InvalidTermId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(TermRequestDTO.Join joinRequest, ConstraintValidatorContext context) {
        if (joinRequest == null || joinRequest.getTerms() == null || joinRequest.getTerms().isEmpty()) {
            return false; // 약관 리스트가 비어있으면 유효하지 않음
        }

        // 요청된 약관 ID가 실제 존재하는지 확인
        boolean allTermsExist = joinRequest.getTerms().stream()
                .allMatch(term -> term.getTermId() != null && termRepositoryService.existsById(term.getTermId()));

        if (!allTermsExist) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.INVALID_TERM_ID.toString()).addConstraintViolation();
        }

        return allTermsExist;
    }
}
