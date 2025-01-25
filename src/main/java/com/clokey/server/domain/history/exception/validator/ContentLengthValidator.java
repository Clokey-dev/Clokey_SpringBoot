package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.exception.annotation.CommentContentLength;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentLengthValidator implements ConstraintValidator<CommentContentLength, String> {

    @Override
    public void initialize(CommentContentLength constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String content, ConstraintValidatorContext context) {
        boolean isValid = content.length() <= 50;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.COMMENT_LENGTH_OUT_OF_RANGE.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
