package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.comment.application.CommentRepositoryService;
import com.clokey.server.domain.history.exception.annotation.ExistCommentNullable;
import com.clokey.server.domain.history.exception.annotation.NoNestedReplies;
import com.clokey.server.domain.model.Comment;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NoNestedRepliesValidator implements ConstraintValidator<NoNestedReplies, Long> {

    private final CommentRepositoryService commentRepositoryService;

    @Override
    public void initialize(com.clokey.server.domain.history.exception.annotation.NoNestedReplies constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long commentId, ConstraintValidatorContext context) {

        Comment parentComment = commentRepositoryService.findById(commentId).get().getComment();

        boolean isValid = parentComment == null;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NESTED_COMMENT.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
