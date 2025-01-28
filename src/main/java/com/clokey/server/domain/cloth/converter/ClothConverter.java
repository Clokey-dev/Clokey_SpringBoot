package com.clokey.server.domain.cloth.converter;

import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.category.domain.entity.Category;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.member.domain.entity.Member;

import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class ClothConverter {

    public static ClothResponseDTO.ClothCreateResult toClothCreateResult(Cloth cloth) {
        return ClothResponseDTO.ClothCreateResult.builder()
                .id(cloth.getId())
                .build();
    }

    public static ClothResponseDTO.ClothPopupViewResult toClothPopupViewResult(Cloth cloth) {

        // CreatedAt으로 등록일자 가져오기 / LocalDateTime -> Date 변환
        Date regDate = java.sql.Date.valueOf(cloth.getCreatedAt().toLocalDate());

        // 요일 가져오기
        String dayOfWeek = cloth.getCreatedAt().getDayOfWeek()
                .getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                .toUpperCase(); // 대문자로 출력

        return ClothResponseDTO.ClothPopupViewResult.builder()
                .id(cloth.getId())
                .regDate(regDate)
                .dayOfWeek(dayOfWeek)
                .imageUrl(cloth.getImage() != null ? cloth.getImage().getImageUrl() : null)
                .name(cloth.getName())
                .seasons(cloth.getSeasons())
                .wearNum(cloth.getWearNum())
                .visibility(cloth.getVisibility())
                .brand(cloth.getBrand())
                .clothUrl(cloth.getClothUrl())
                .categoryId(cloth.getCategory().getId())
                .build();
    }

    public static ClothResponseDTO.ClothEditViewResult toClothEditViewResult(Cloth cloth) {

        return ClothResponseDTO.ClothEditViewResult.builder()
                .id(cloth.getId())
                .name(cloth.getName())
                .seasons(cloth.getSeasons())
                .tempUpperBound(cloth.getTempUpperBound())
                .tempLowerBound(cloth.getTempLowerBound())
                .thicknessLevel(cloth.getThicknessLevel())
                .visibility(cloth.getVisibility())
                .clothUrl(cloth.getClothUrl())
                .brand(cloth.getBrand())
                .imageUrl(cloth.getImage() != null ? cloth.getImage().getImageUrl() : null)
                .categoryId(cloth.getCategory().getId())
                .build();
    }

    public static ClothResponseDTO.ClothDetailViewResult toClothDetailViewResult(Cloth cloth) {

        return ClothResponseDTO.ClothDetailViewResult.builder()
                .id(cloth.getId())
                .name(cloth.getName())
                .wearNum(cloth.getWearNum())
                .seasons(cloth.getSeasons())
                .tempUpperBound(cloth.getTempUpperBound())
                .tempLowerBound(cloth.getTempLowerBound())
                .thicknessLevel(cloth.getThicknessLevel())
                .visibility(cloth.getVisibility())
                .clothUrl(cloth.getClothUrl())
                .brand(cloth.getBrand())
                .imageUrl(cloth.getImage() != null ? cloth.getImage().getImageUrl() : null)
                .memberId(cloth.getMember().getId())
                .categoryId(cloth.getCategory().getId())
                .createdAt(cloth.getCreatedAt())
                .updatedAt(cloth.getUpdatedAt())
                .build();
    }

    public static Cloth toCloth(Long memberId, ClothRequestDTO.ClothCreateOrUpdateRequest request) {
        return Cloth.builder()
                .name(request.getName())
                .seasons(request.getSeasons()) // List<Season> 직접 할당
                .tempUpperBound(request.getTempUpperBound())
                .tempLowerBound(request.getTempLowerBound())
                .thicknessLevel(request.getThicknessLevel())
                .visibility(request.getVisibility())
                .clothUrl(request.getClothUrl())
                .brand(request.getBrand())
                .category(Category.builder()
                        .id(request.getCategoryId())
                        .build()) // Category를 간단히 생성 (참조만 설정)
                .member(Member.builder()
                        .id(memberId)
                        .build()) // Member를 간단히 생성 (참조만 설정)
                .build();
    }
}
