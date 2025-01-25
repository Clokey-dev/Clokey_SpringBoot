package com.clokey.server.domain.history.exception.validator;

import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.domain.history.exception.annotation.HistoryImageQuantityLimit;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HistoryImageQuantityLimitValidator implements ConstraintValidator<HistoryImageQuantityLimit, List<MultipartFile>> {

    private final HistoryRepositoryService historyRepositoryService;

    @Override
    public void initialize(HistoryImageQuantityLimit constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<MultipartFile> images, ConstraintValidatorContext context) {

        if(images == null || images.isEmpty()){
            return true;
        }

        boolean isValid = images.size()<=10;

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.IMAGE_QUANTITY_OVER_HISTORY_IMAGE_LIMIT.toString()).addConstraintViolation();
        }

        return isValid;

    }
}

