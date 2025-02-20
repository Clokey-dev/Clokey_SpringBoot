package com.clokey.server.domain.cloth.exception.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.cloth.exception.annotation.ClothImagePresence;
import com.clokey.server.global.error.code.status.ErrorStatus;

@Component
@RequiredArgsConstructor
public class ClothImagePresenceValidator implements ConstraintValidator<ClothImagePresence, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile imageFile, ConstraintValidatorContext context) {

        // 파일이 비어있는지, 즉 업로드된 이미지가 없는지 체크
        if (imageFile == null || imageFile.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.NO_CLOTH_IMAGE_INPUT.toString())
                    .addConstraintViolation();
            return false;
        }

        return true;  // 유효성 통과

    }
}
