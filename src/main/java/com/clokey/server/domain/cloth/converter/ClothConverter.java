package com.clokey.server.domain.cloth.converter;

import com.clokey.server.domain.cloth.dto.ClothResponseDto;
import com.clokey.server.domain.model.Cloth;
import com.clokey.server.domain.model.ClothImage;

import java.util.List;

public class ClothConverter {

    public static ClothResponseDto.ClothReadResult toClothReadResult(Cloth cloth) {
        return ClothResponseDto.ClothReadResult.builder()
                .id(cloth.getId())
                .name(cloth.getName())
                .wearNum(cloth.getWearNum())
                .regDate(cloth.getRegDate())
                .season(cloth.getSeason())
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
