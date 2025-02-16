package com.clokey.server.domain.search.exception.validator;

import com.clokey.server.domain.history.application.CommentRepositoryService;
import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.exception.annotation.ParentCommentConditions;
import com.clokey.server.domain.search.exception.annotation.KeywordNotNull;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeywordNotNullValidator implements ConstraintValidator<KeywordNotNull, String> {

    private final CommentRepositoryService commentRepositoryService;

    @Override
    public void initialize(KeywordNotNull constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String keyword, ConstraintValidatorContext context) {

        //null인 경우 검증 실패
        if(keyword == null||keyword.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_PARAMETER.toString()).addConstraintViolation();

            return false;
        }

        return true;
    }
}
