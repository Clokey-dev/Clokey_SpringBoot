package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.comment.application.CommentRepositoryService;
import com.clokey.server.domain.history.exception.annotation.ParentCommentConditions;
import com.clokey.server.domain.model.Comment;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ParentCommentsConditionsValidator implements ConstraintValidator<ParentCommentConditions, Long> {

    private final CommentRepositoryService commentRepositoryService;

    @Override
    public void initialize(ParentCommentConditions constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long commentId, ConstraintValidatorContext context) {

        //null인 경우 검증 통과
        if(commentId == null) {
            return true;
        }

        //null이 아닌 경우 존재를 확인함.
        if (!commentRepositoryService.commentExist(commentId)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_COMMENT.toString()).addConstraintViolation();

            return false;
        }

        //존재하는 경우 댓글 대댓글 깊이 검사
        Comment parentComment = commentRepositoryService.findById(commentId).get().getComment();

        if (parentComment != null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NESTED_COMMENT.toString()).addConstraintViolation();

            return false;
        }

        return true;
    }
}
