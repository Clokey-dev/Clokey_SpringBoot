package com.clokey.server.domain.cloth.dto;

import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;
import com.clokey.server.domain.model.entity.enums.Visibility;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

public class ClothResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothCreateResult{
        Long id;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothReadResult{
        Long id;
        String name;
        int wearNum;
        LocalDate regDate;
        Season season;
        int tempUpperBound;
        int tempLowerBound;
        ThicknessLevel thicknessLevel;
        Visibility visibility;
        String clothUrl;
        String brand;
        List<String> images;
        Long memberId;
        Long categoryId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothUpdateResult{
        Long id;
        List<String> updateFields;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothPreview {
        private Long id;
        private String name;
        private String mainImage;
        private int wearNum;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryClothReadResult {
        private List<ClothPreview> clothes;
        private int listSize;
        private int totalPage;
        private long totalElements;
        private boolean isFirst;
        private boolean isLast;
    }

}
