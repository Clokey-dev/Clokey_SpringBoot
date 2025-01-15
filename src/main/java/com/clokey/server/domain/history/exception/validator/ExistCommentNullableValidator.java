package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.comment.application.CommentRepositoryService;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistCommentNullableValidator implements ConstraintValidator<com.clokey.server.domain.history.exception.annotation.ExistCommentNullable, Long> {

    private final CommentRepositoryService commentRepositoryService;

    @Override
    public void initialize(com.clokey.server.domain.history.exception.annotation.ExistCommentNullable constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long commentId, ConstraintValidatorContext context) {

        if(commentId == null) {
            return true;
        }

        boolean isValid = commentRepositoryService.commentExist(commentId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_COMMENT.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
