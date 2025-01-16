package com.clokey.server.domain.cloth.dto;

import com.clokey.server.domain.model.enums.Season;
import com.clokey.server.domain.model.enums.ThicknessLevel;
import com.clokey.server.domain.model.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ClothRequestDto {

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
