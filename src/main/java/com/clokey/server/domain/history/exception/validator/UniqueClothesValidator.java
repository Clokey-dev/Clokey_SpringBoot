package com.clokey.server.domain.history.exception.validator;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.history.exception.annotation.UniqueClothes;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class UniqueClothesValidator implements ConstraintValidator<UniqueClothes, List<Long>> {

    @Override
    public void initialize(UniqueClothes constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(List<Long> clothes, ConstraintValidatorContext context) {

        if (clothes == null || clothes.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_CLOTH_FOR_HISTORY.toString()).addConstraintViolation();
            return false; // 비어있는 경우는 유효하다고 판단
        }

        Set<Long> uniqueClothes = new HashSet<>(clothes); // Set에 리스트를 추가
        boolean isValid = uniqueClothes.size() == clothes.size();

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.DUPLICATE_CLOTHES_FOR_HISTORY.toString()).addConstraintViolation();
        }

        return isValid;

    }
}
