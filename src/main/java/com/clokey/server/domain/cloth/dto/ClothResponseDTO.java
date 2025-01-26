package com.clokey.server.domain.cloth.dto;

import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;
import com.clokey.server.domain.model.entity.enums.Visibility;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ClothResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothCreateOrUpdateResult {
        private Long id;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothPopupViewResult {
        private Long id;
        private Date regDate;
        private String dayOfWeek;
        private String imageUrl;
        private String name;
        private List<Season> seasons;
        private int wearNum;
        private Visibility visibility;
        private String brand;
        private String clothUrl;
        private Long categoryId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothEditViewResult {
        private Long id;
        private String name;
        private List<Season> seasons;
        private int tempUpperBound;
        private int tempLowerBound;
        private ThicknessLevel thicknessLevel;
        private Visibility visibility;
        private String clothUrl;
        private String brand;
        private String imageUrl;
        private Long categoryId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothDetailViewResult {
        private Long id;
        private String name;
        private int wearNum;
        private List<Season> seasons;
        private int tempUpperBound;
        private int tempLowerBound;
        private ThicknessLevel thicknessLevel;
        private Visibility visibility;
        private String clothUrl;
        private String brand;
        private String imageUrl;
        private Long memberId;
        private Long categoryId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
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
