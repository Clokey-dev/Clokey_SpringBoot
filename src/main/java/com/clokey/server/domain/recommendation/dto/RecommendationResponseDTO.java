package com.clokey.server.domain.recommendation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class RecommendationResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyClothesResult {
        List<DailyClothResult> recommendations;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyClothResult {
        Long clothId;
        String imageUrl;
        String clothName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyNewsResult {
        private List<Recommend> recommend;
        private List<Closet> closet;
        private List<Calendar> calendar;
        private List<People> people;
        private Integer listSize;
        private Integer totalPage;
        private Integer totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Recommend {
        private String imageUrl;
        private String subTitle;
        private String hashtag;
        private LocalDateTime date;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Closet {
        private Long memberId;
        private String clokeyId;
        private String profileImage;
        private List<Long> clothesId;
        private List<String> images;
        private LocalDateTime date;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Calendar {
        private LocalDate date;
        private Long memberId;
        private String clokeyId;
        private String profileImage;
        private List<Event> events;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Event {
        private Long historyId;
        private String imageUrl;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class People {
        private Long memberId;
        private String clokeyId;
        private String profileImage;
        private String imageUrl;
    }
}
