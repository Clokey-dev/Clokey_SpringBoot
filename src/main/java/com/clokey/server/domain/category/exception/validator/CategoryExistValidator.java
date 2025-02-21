package com.clokey.server.domain.category.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.category.application.CategoryRepositoryService;
import com.clokey.server.domain.category.exception.annotation.CategoryExist;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class CategoryExistValidator implements ConstraintValidator<CategoryExist, Long> {

    private final CategoryRepositoryService categoryRepositoryService;

    @Override
    public boolean isValid(Long categoryId, ConstraintValidatorContext context) {
        if(categoryId==0)
            return true;

        boolean isValid = categoryRepositoryService.existsById(categoryId);

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_SUCH_CATEGORY.toString()).addConstraintViolation();
        }

        return isValid;
    }
}
