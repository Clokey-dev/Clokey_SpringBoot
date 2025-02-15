package com.clokey.server.domain.notification.dto;

import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.ReadStatus;
import com.clokey.server.domain.model.entity.enums.RedirectType;
import jakarta.persistence.*;
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
    public static class GetNotificationResult {
        private List<NotificationResult> notificationResults;
        private Integer totalPage;
        private Integer totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationResult {
        private Long notificationId;
        private String content;
        private String notificationImageUrl;
        private String redirectInfo;
        private RedirectType redirectType;
        private Boolean isRead;
        private LocalDate createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnReadNotificationCheckResult {
       boolean unReadNotificationExist;
    }

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
}
