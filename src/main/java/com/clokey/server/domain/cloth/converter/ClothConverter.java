package com.clokey.server.domain.cloth.converter;

import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.model.entity.Cloth;
import com.clokey.server.domain.model.entity.ClothImage;

import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class ClothConverter {

    public static ClothResponseDTO.ClothReadResult toClothReadResult(Cloth cloth) {

        // CreatedAt으로 등록일자 가져오기 / LocalDateTime -> Date 변환
        Date regDate = java.sql.Date.valueOf(cloth.getCreatedAt().toLocalDate());

        // 요일 가져오기
        String dayOfWeek = cloth.getCreatedAt().getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                .toUpperCase(); // 대문자로 출력

        return ClothResponseDTO.ClothReadResult.builder()
                .id(cloth.getId())
                .name(cloth.getName())
                .wearNum(cloth.getWearNum())
                .regDate(regDate)
                .dayOfWeek(dayOfWeek)
                .seasons(cloth.getSeasons())
                .tempUpperBound(cloth.getTempUpperBound())
                .tempLowerBound(cloth.getTempLowerBound())
                .thicknessLevel(cloth.getThicknessLevel())
                .visibility(cloth.getVisibility())
                .clothUrl(cloth.getClothUrl())
                .brand(cloth.getBrand())
                .images(cloth.getImages().stream()
                        .map(ClothImage::getImageUrl)
                        .toList()) // ClothImage의 imageUrl만 추출
                .memberId(cloth.getMember().getId())
                .categoryId(cloth.getCategory().getId())
                .build();
    }
}
