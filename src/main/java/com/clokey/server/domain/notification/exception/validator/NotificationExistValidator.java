package com.clokey.server.domain.notification.exception.validator;

import com.clokey.server.domain.history.application.CommentRepositoryService;
import com.clokey.server.domain.history.exception.annotation.CommentExist;
import com.clokey.server.domain.notification.application.NotificationRepositoryService;
import com.clokey.server.domain.notification.domain.repository.NotificationRepository;
import com.clokey.server.domain.notification.exception.annotation.NotificationExist;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationExistValidator implements ConstraintValidator<NotificationExist, Long> {

    private final NotificationRepositoryService notificationRepositoryService;

    @Override
    public void initialize(NotificationExist constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Long notificationId, ConstraintValidatorContext context) {
        boolean isValid = notificationRepositoryService.existsById(notificationId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_NOTIFICATION.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
