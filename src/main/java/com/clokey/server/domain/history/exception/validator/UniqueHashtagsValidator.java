package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.exception.annotation.UniqueHashtags;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class UniqueHashtagsValidator implements ConstraintValidator<UniqueHashtags, List<String>> {

    @Override
    public void initialize(UniqueHashtags constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<String> hashtags, ConstraintValidatorContext context) {

        if (hashtags == null || hashtags.isEmpty()) {
            return true; // 비어있는 경우는 유효하다고 판단
        }

        Set<String> uniqueHashtags = new HashSet<>(hashtags); // Set에 리스트를 추가
        boolean isValid = uniqueHashtags.size() == hashtags.size();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.DUPLICATE_HASHTAGS.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
