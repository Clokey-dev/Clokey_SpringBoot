package com.clokey.server.domain.cloth.dto;

import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;
import com.clokey.server.domain.model.entity.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ClothRequestDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothCreateRequest{
        String name;
        Season season;
        int tempUpperBound;
        int tempLowerBound;
        ThicknessLevel thicknessLevel;
        Visibility visibility;
        String clothUrl;
        String brand;
        Long memberId;
        Long categoryId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClothUpdateRequest{
        String name;
        Season season;
        int tempUpperBound;
        int tempLowerBound;
        ThicknessLevel thicknessLevel;
        Visibility visibility;
        String clothUrl;
        String brand;
        Long memberId;
        Long categoryId;
    }
}
