package com.clokey.server.domain.notification.dto;

import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class NotificationResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryLikeNotificationResult {
        String content;
        String memberProfileUrl;
        Long historyId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewFollowerNotificationResult {
        String content;
        String memberProfileUrl;
        String clokeyId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryCommentNotificationResult {
        String content;
        String memberProfileUrl;
        Long historyId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReplyNotificationResult {
        String content;
        String memberProfileUrl;
        Long historyId;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewHistoryNotificationResult {
        String content;
        String historyImageUrl;
        Long historyId;
    }
}
