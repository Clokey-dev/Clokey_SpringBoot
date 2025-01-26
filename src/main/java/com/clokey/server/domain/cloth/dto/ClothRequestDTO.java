package com.clokey.server.domain.cloth.dto;

import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;
import com.clokey.server.domain.model.entity.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ClothRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothCreateOrUpdateRequest {
        private String name;
        private List<Season> seasons;
        private int tempUpperBound;
        private int tempLowerBound;
        private ThicknessLevel thicknessLevel;
        private Visibility visibility;
        private String clothUrl;
        private String brand;
        private Long categoryId;
    }
}
