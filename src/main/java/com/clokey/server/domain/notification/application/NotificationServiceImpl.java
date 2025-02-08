package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.NotificationType;
import com.clokey.server.domain.notification.domain.entity.ClokeyNotification;
import com.clokey.server.domain.notification.dto.NotificationResponseDTO;
import com.clokey.server.domain.notification.exception.NotificationException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final HistoryLikedValidator historyLikedValidator;
    private final HistoryRepositoryService historyRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationRepositoryService notificationRepositoryService;

    private static final String HISTORY_LIKED_NOTIFICATION_CONTENT = "%s님이 나의 기록에 좋아요를 눌렀습니다.";

    @Override
    @Transactional
    public NotificationResponseDTO.HistoryLikeNotificationResult sendHistoryLikeNotification(Long memberId, Long historyId) {

        historyLikedValidator.validateIsLiked(historyId,memberId,true);

        Member historyWriter = historyRepositoryService.findById(historyId).getMember();
        Member likedMember = memberRepositoryService.findMemberById(memberId);

        //로그아웃 상태가 아니고 약관동의를 한 경우에만 알림이 전송됩니다.
        if(historyWriter.getDeviceToken() != null && historyWriter.getRefreshToken() != null) {
            String content = String.format(HISTORY_LIKED_NOTIFICATION_CONTENT, likedMember.getNickname());
            String likedMemberProfileUrl = likedMember.getProfileImageUrl();

            Notification notification = Notification.builder()
                    .setBody(content)
                    .setImage(likedMemberProfileUrl)
                    .build();

            Message message = Message.builder()
                    .setToken(historyWriter.getDeviceToken())
                    .setNotification(notification)
                    .putData("historyId", historyId)
                    .build();
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                throw new NotificationException(ErrorStatus.NOTIFICATION_FIREBASE_ERROR);
            }

            ClokeyNotification clokeyNotification = ClokeyNotification.builder()
                    .member(historyWriter)
                    .content(content)
                    .notificationImageUrl(likedMemberProfileUrl)
                    .redirectInfo(historyId)
                    .notificationType(NotificationType.HISTORY_REDIRECT)
                    .build();

            notificationRepositoryService.save(clokeyNotification);

            return NotificationResponseDTO.HistoryLikeNotificationResult.builder()
                    .content(content)
                    .historyId(historyId)
                    .memberProfileUrl(likedMemberProfileUrl)
                    .build();
        }
        return null;
    }
}
