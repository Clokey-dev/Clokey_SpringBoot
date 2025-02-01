package com.clokey.server.domain.cloth.dto;

import com.clokey.server.domain.cloth.exception.annotation.ClothCreateOrUpdateFormat;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.model.entity.enums.ThicknessLevel;
import com.clokey.server.domain.model.entity.enums.Visibility;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        @NotNull
        @NotBlank
        private String name;
        @NotNull
        @NotBlank
        private List<Season> seasons;
        @NotNull
        @NotBlank
        @Min(-20)
        @Max(40)
        private Integer tempUpperBound;
        @NotNull
        @NotBlank
        @Min(-20)
        @Max(40)
        private Integer tempLowerBound;
        @NotNull
        @NotBlank
        private ThicknessLevel thicknessLevel;
        @NotNull
        @NotBlank
        private Visibility visibility;
        private String clothUrl;
        private String brand;
    }
}
