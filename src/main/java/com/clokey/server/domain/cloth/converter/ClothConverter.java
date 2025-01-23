package com.clokey.server.domain.cloth.converter;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.model.entity.Category;
import com.clokey.server.domain.model.entity.Cloth;
import com.clokey.server.domain.model.entity.ClothImage;
import com.clokey.server.domain.model.entity.Member;

import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class ClothConverter {

    public static ClothResponseDTO.ClothCreateResult toClothCreateResult(Cloth cloth) {
        return ClothResponseDTO.ClothCreateResult.builder()
                .id(cloth.getId())
                .build();
    }

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

    /*
    public static ClothResponseDTO.ClothUpdateResult toClothUpdateResult(Cloth cloth) {
        return ClothResponseDTO.ClothUpdateResult.builder()
                .id(cloth.getId())
                .updateFields()
                .build();
    }*/

    public static Cloth toCloth(ClothRequestDTO.ClothCreateRequest request) {
        return Cloth.builder()
                .name(request.getName())
                .seasons(request.getSeasons()) // List<Season> 직접 할당
                .tempUpperBound(request.getTempUpperBound())
                .tempLowerBound(request.getTempLowerBound())
                .thicknessLevel(request.getThicknessLevel())
                .visibility(request.getVisibility())
                .clothUrl(request.getClothUrl())
                .brand(request.getBrand())
                .member(Member.builder()
                        .id(request.getMemberId())
                        .build()) // Member를 간단히 생성 (참조만 설정)
                .category(Category.builder()
                        .id(request.getCategoryId())
                        .build()) // Category를 간단히 생성 (참조만 설정)
                .build();
    }
}
