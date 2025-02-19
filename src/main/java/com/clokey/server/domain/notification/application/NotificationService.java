package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.notification.dto.NotificationResponseDTO;

public interface NotificationService {

    NotificationResponseDTO.HistoryLikeNotificationResult sendHistoryLikeNotification(Long memberId, Long historyId);

    NotificationResponseDTO.NewFollowerNotificationResult sendNewFollowerNotification(String followedMemberClokeyId,Long followingMemberId);

    NotificationResponseDTO.HistoryCommentNotificationResult sendHistoryCommentNotification(Long historyId, Long commentId, Long memberId);

    NotificationResponseDTO.ReplyNotificationResult sendReplyNotification(Long commentId, Long replyId, Long memberId);

    NotificationResponseDTO.UnReadNotificationCheckResult checkUnReadNotifications(Long memberId);

    void readNotification(Long notificationId, Long memberId);

    void sendOneYearAgoNotification(Long memberId);

    void sendTodayTemperatureNotification(Integer temperatureDiff, Long memberId);

    void sendSeasonsNotification(Season season, Long memberId);

    NotificationResponseDTO.GetNotificationResult getNotifications(Long memberId, Integer page);

    void readAllNotification(Long memberId);
}
