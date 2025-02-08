package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.notification.dto.NotificationResponseDTO;
import com.clokey.server.domain.notification.exception.NotificationException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final HistoryLikedValidator historyLikedValidator;
    private final HistoryRepositoryService historyRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final FirebaseMessaging firebaseMessaging;

    private static final String HISTORY_LIKED_NOTIFICATION_CONTENT = "%s님이 나의 기록에 좋아요를 눌렀습니다.";

    @Override
    public NotificationResponseDTO.HistoryLikeNotificationResult sendHistoryLikeNotification(Long memberId, Long historyId) {

        historyLikedValidator.validateIsLiked(historyId,memberId,true);

        Member historyWriter = historyRepositoryService.findById(historyId).getMember();
        Member likedMember = memberRepositoryService.findMemberById(memberId);

        if(historyWriter.getDeviceToken() != null) {
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
                throw new NotificationException()
            }


        }



                try {
                    firebaseMessaging.send(message);
                    return "알림을 성공적으로 전송했습니다. targetUserId=" + requestDto.getTargetUserId();
                } catch (FirebaseMessagingException e) {
                    e.printStackTrace();
                    return "알림 보내기를 실패하였습니다. targetUserId=" + requestDto.getTargetUserId();
                }
            } else {
                return "서버에 저장된 해당 유저의 FirebaseToken이 존재하지 않습니다. targetUserId=" + requestDto.getTargetUserId();
            }

        } else {
            return "해당 유저가 존재하지 않습니다. targetUserId=" + requestDto.getTargetUserId();
        }
        return null;
    }
}
