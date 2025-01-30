package com.clokey.server.domain.category.exception.validator;

import com.clokey.server.domain.category.application.CategoryRepositoryService;
import com.clokey.server.domain.category.exception.annotation.CategoryExist;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryExistValidator implements ConstraintValidator<CategoryExist, Long> {

    private final CategoryRepositoryService categoryRepositoryService;

    @Override
    public boolean isValid(Long clothId, ConstraintValidatorContext context) {
        boolean isValid = categoryRepositoryService.existsById(clothId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_CATEGORY.toString()).addConstraintViolation();
        }

        return isValid;
    }
}


