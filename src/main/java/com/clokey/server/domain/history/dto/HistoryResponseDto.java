package com.clokey.server.domain.history.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class HistoryResponseDto {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class dayViewResult{
        Long userId;
        String contents;
        List<String> imageUrl;
        List<String> hashtags;
        boolean visibility;
        int likeCount;
        boolean isLiked;
        LocalDate date;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class monthViewResult{
        Long userId;
        List<historyResult> histories;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class historyResult{
        Long historyId;
        LocalDate date;
        String imageUrl;
    }
}



