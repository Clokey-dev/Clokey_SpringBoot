package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.application.CommentRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.exception.annotation.CommentExist;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentExistValidator implements ConstraintValidator<CommentExist, Long> {

    private final CommentRepositoryService commentRepositoryService;

    @Override
    public void initialize(CommentExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long commentId, ConstraintValidatorContext context) {
        boolean isValid = commentRepositoryService.existsById(commentId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_COMMENT.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
