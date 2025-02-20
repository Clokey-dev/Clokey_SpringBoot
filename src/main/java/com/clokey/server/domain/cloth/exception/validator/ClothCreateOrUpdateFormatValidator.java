package com.clokey.server.domain.cloth.exception.validator;

import org.springframework.stereotype.Component;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.exception.annotation.ClothCreateOrUpdateFormat;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;
import com.clokey.server.domain.model.entity.enums.Visibility;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class ClothCreateOrUpdateFormatValidator implements ConstraintValidator<ClothCreateOrUpdateFormat, ClothRequestDTO.ClothCreateOrUpdateRequest> {

    @Override
    public boolean isValid(ClothRequestDTO.ClothCreateOrUpdateRequest request, ConstraintValidatorContext context) {

        // 필수 필드가 null인지 확인
        if (request.getName() == null || request.getName().isEmpty()) {
            addViolation(context, ErrorStatus.INVALID_CREATE_OR_UPDATE_CLOTH_FORMAT);
            return false;
        }
        if (request.getSeasons() == null || request.getSeasons().isEmpty()) {
            addViolation(context, ErrorStatus.INVALID_CREATE_OR_UPDATE_CLOTH_FORMAT);
            return false;
        }
        if (request.getTempUpperBound() == null || request.getTempLowerBound() == null) {
            addViolation(context, ErrorStatus.INVALID_CREATE_OR_UPDATE_CLOTH_FORMAT);
            return false;
        }

        // Visibility 값 검증
        if (request.getVisibility() == null || !isValidVisibility(String.valueOf(request.getVisibility()))) {
            addViolation(context, ErrorStatus.CLOTH_VISIBILITY_INVALID);
            return false;
        }

        // ThicknessLevel 값 검증
        if (request.getThicknessLevel() == null || !isValidThickness(String.valueOf(request.getThicknessLevel()))) {
            addViolation(context, ErrorStatus.CLOTH_THICKNESS_INVALID);
            return false;
        }

        // 온도 범위 검증
        if (request.getTempUpperBound() < -20 || request.getTempUpperBound() > 40) {
            addViolation(context, ErrorStatus.CLOTH_TEMP_OUT_OF_RANGE);
            return false;
        }

        if (request.getTempLowerBound() < -20 || request.getTempLowerBound() > 40) {
            addViolation(context, ErrorStatus.CLOTH_TEMP_OUT_OF_RANGE);
            return false;
        }

        // 온도 순서 검증 (하한 온도가 상한 온도보다 크면 안됨)
        if (request.getTempLowerBound() > request.getTempUpperBound()) {
            addViolation(context, ErrorStatus.CLOTH_TEMP_ORDER_INVALID);
            return false;
        }

        return true;
    }

    // Visibility 값 검증
    public boolean isValidVisibility(String visibility) {
        try {
            // Enum에 정의된 값으로 변환 가능한지 체크
            Visibility.valueOf(visibility);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Enum에 없는 값일 경우 false 반환
        }
    }

    // ThicknessLevel 값 검증
    public boolean isValidThickness(String thicknessLevel) {
        try {
            // Enum에 정의된 값으로 변환 가능한지 체크
            ThicknessLevel.valueOf(thicknessLevel);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Enum에 없는 값일 경우 false 반환
        }
    }

    // ConstraintViolation 추가하는 메소드
    private void addViolation(ConstraintValidatorContext context, ErrorStatus errorStatus) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(errorStatus.toString()) // 오류 메시지 설정
                .addConstraintViolation(); // ConstraintViolation 추가
    }
}
