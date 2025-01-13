package com.clokey.server.domain.history.dto;

import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class HistoryRequestDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class likeStatusChange {

        @HistoryExist
        Long historyId;

        @NotNull
        boolean isLiked;
    }
}
