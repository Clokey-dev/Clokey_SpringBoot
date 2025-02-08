package com.clokey.server.domain.notification.application;

import com.clokey.server.domain.history.application.CommentRepositoryService;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.application.FollowRepositoryService;
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
@Transactional
public class NotificationServiceImpl implements NotificationService{

    private final HistoryLikedValidator historyLikedValidator;
    private final HistoryRepositoryService historyRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final FirebaseMessaging firebaseMessaging;
    private final NotificationRepositoryService notificationRepositoryService;
    private final FollowRepositoryService followRepositoryService;
    private final CommentRepositoryService commentRepositoryService;

    private static final String HISTORY_LIKED_NOTIFICATION_CONTENT = "%s님이 나의 기록에 좋아요를 눌렀습니다.";
    private static final String NEW_FOLLOWER_NOTIFICATION_CONTENT = "%s님이 회원님의 옷장을 팔로우하기 시작했습니다.";
    private static final String HISTORY_COMMENT_NOTIFICATION_CONTENT = "%s님이 나의 기록에 댓글을 남겼습니다.";
    private static final String COMMENT_REPLY_CONTENT = "%s님이 나의 댓글에 답장을 남겼습니다.";

    @Override
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

    @Override
    public NotificationResponseDTO.NewFollowerNotificationResult sendNewFollowerNotification(String followedMemberClokeyId, Long followingMemberId) {

        Member followedMember = memberRepositoryService.findMemberByClokeyId(followedMemberClokeyId);
        Member followingMember = memberRepositoryService.findMemberById(followingMemberId);

        checkFollowing(followingMemberId,followedMember.getId());

        //로그아웃 상태가 아니고 약관동의를 한 경우에만 알림이 전송됩니다.
        if(followedMember.getDeviceToken() != null && followedMember.getRefreshToken() != null) {
            String content = String.format(NEW_FOLLOWER_NOTIFICATION_CONTENT,followingMember.getNickname());
            String followingMemberProfileUrl = followingMember.getProfileImageUrl();

            Notification notification = Notification.builder()
                    .setBody(content)
                    .setImage(followingMemberProfileUrl)
                    .build();

            Message message = Message.builder()
                    .setToken(followedMember.getDeviceToken())
                    .setNotification(notification)
                    .putData("clokeyID", followingMember.getClokeyId())
                    .build();

            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                throw new NotificationException(ErrorStatus.NOTIFICATION_FIREBASE_ERROR);
            }

            ClokeyNotification clokeyNotification = ClokeyNotification.builder()
                    .member(followedMember)
                    .content(content)
                    .notificationImageUrl(followingMemberProfileUrl)
                    .redirectInfo(followingMember.getClokeyId())
                    .notificationType(NotificationType.MEMBER_REDIRECT)
                    .build();

            notificationRepositoryService.save(clokeyNotification);

            return NotificationResponseDTO.NewFollowerNotificationResult.builder()
                    .content(content)
                    .memberProfileUrl(followingMemberProfileUrl)
                    .clokeyId(followingMember.getClokeyId())
                    .build();
        }
        return null;
    }


    private void checkFollowing(Long followingId, Long followedId){
        if(!followRepositoryService.existsByFollowing_IdAndFollowed_Id(followingId,followedId)){
            throw new NotificationException(ErrorStatus.NOTIFICATION_NOT_FOLLOWING);
        }
    }

    @Override
    public NotificationResponseDTO.HistoryCommentNotificationResult sendHistoryCommentNotification(Long historyId, Long commentId, Long memberId) {

        checkMyComment(commentId,memberId);
        checkHistoryComment(commentId,historyId);

        Member historyWriter = historyRepositoryService.findById(historyId).getMember();
        Member commentWriter = memberRepositoryService.findMemberById(memberId);

        //로그아웃 상태가 아니고 약관동의를 한 경우에만 알림이 전송됩니다.
        if(historyWriter.getDeviceToken() != null && historyWriter.getRefreshToken() != null) {
            String content = String.format(HISTORY_COMMENT_NOTIFICATION_CONTENT,commentWriter.getNickname());
            String commentWriterProfileUrl = commentWriter.getProfileImageUrl();

            Notification notification = Notification.builder()
                    .setBody(content)
                    .setImage(commentWriterProfileUrl)
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
                    .notificationImageUrl(commentWriterProfileUrl)
                    .redirectInfo(historyId)
                    .notificationType(NotificationType.HISTORY_REDIRECT)
                    .build();

            notificationRepositoryService.save(clokeyNotification);

            return NotificationResponseDTO.HistoryCommentNotificationResult.builder()
                    .content(content)
                    .historyId(historyId)
                    .memberProfileUrl(commentWriterProfileUrl)
                    .build();

        }


        return null;
    }

    private void checkMyComment(Long commentId, Long memberId){
        if(!commentRepositoryService.existsByIdAndMemberId(commentId,memberId)){
            throw new NotificationException(ErrorStatus.NOTIFICATION_NOT_MY_COMMENT);
        }
    }

    private void checkHistoryComment(Long commentId, Long historyId){
        if(!commentRepositoryService.existsByIdAndHistoryId(commentId,historyId)){
            throw new NotificationException(ErrorStatus.NOTIFICATION_COMMENT_NOT_FROM_HISTORY);
        }
    }

    @Override
    public NotificationResponseDTO.ReplyNotificationResult sendReplyNotification(Long commentId, Long replyId, Long memberId) {

        checkMyComment(replyId,memberId);
        checkParentComment(commentId,replyId);

        Member commentWriter = commentRepositoryService.findById(commentId).getMember();
        Member replyWriter = commentRepositoryService.findById(replyId).getMember();

        //로그아웃 상태가 아니고 약관동의를 한 경우에만 알림이 전송됩니다.
        if(commentWriter.getDeviceToken() != null && commentWriter.getRefreshToken() != null) {
            String content = String.format(COMMENT_REPLY_CONTENT,replyWriter.getNickname());
            String replyWriterProfileUrl = replyWriter.getProfileImageUrl();
            Long historyId = commentRepositoryService.findById(commentId).getHistory().getId();

            Notification notification = Notification.builder()
                    .setBody(content)
                    .setImage(replyWriterProfileUrl)
                    .build();

            Message message = Message.builder()
                    .setToken(commentWriter.getDeviceToken())
                    .setNotification(notification)
                    .putData("historyId", historyId)
                    .build();
            try {
                firebaseMessaging.send(message);
            } catch (FirebaseMessagingException e) {
                throw new NotificationException(ErrorStatus.NOTIFICATION_FIREBASE_ERROR);
            }

            ClokeyNotification clokeyNotification = ClokeyNotification.builder()
                    .member(commentWriter)
                    .content(content)
                    .notificationImageUrl(replyWriterProfileUrl)
                    .redirectInfo(historyId)
                    .notificationType(NotificationType.HISTORY_REDIRECT)
                    .build();

            notificationRepositoryService.save(clokeyNotification);
        }

        return null;
    }

    private void checkParentComment(Long commentId, Long replyId){
        Comment reply = commentRepositoryService.findById(replyId);
        if(!reply.getComment().getId().equals(commentId)){
            throw new NotificationException(ErrorStatus.NOTIFICATION_NOT_PARENT_COMMENT_OF_REPLY);
        }
    }


}
