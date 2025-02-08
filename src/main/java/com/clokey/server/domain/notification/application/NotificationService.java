package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.notification.dto.NotificationResponseDTO;

public interface NotificationService {

    NotificationResponseDTO.HistoryLikeNotificationResult sendHistoryLikeNotification(Long memberId, Long historyId);

    NotificationResponseDTO.NewFollowerNotificationResult sendNewFollowerNotification(String followedMemberClokeyId,Long followingMemberId);

    NotificationResponseDTO.HistoryCommentNotificationResult sendHistoryCommentNotification(Long historyId, Long commentId, Long memberId);

    NotificationResponseDTO.ReplyNotificationResult sendReplyNotification(Long commentId, Long replyId, Long memberId);
}
