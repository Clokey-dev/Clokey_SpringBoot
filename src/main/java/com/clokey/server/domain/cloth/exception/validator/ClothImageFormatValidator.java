package com.clokey.server.domain.cloth.exception.validator;

import com.clokey.server.domain.cloth.exception.annotation.ClothImageFormat;
import com.clokey.server.global.error.code.status.ErrorStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ClothImageFormatValidator implements ConstraintValidator<ClothImageFormat, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile imageFile, ConstraintValidatorContext context) {

        // 이미지 형식이 올바른지 체크 (예: jpg, png, jpeg 등)
        if (imageFile == null) {
            return true;
        }
        String contentType = imageFile.getContentType();
        if (!contentType.startsWith("image/")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ErrorStatus.CLOTH_INVAID_IMAGE_FORMAT.toString())
                    .addConstraintViolation();
            return false;
        }
        return true;  // 유효성 통과
    }
}
