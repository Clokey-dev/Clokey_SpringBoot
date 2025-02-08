package com.clokey.server.domain.notification.api;


import com.clokey.server.domain.history.exception.annotation.CommentExist;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.domain.member.exception.annotation.IdValid;
import com.clokey.server.domain.notification.application.NotificationService;
import com.clokey.server.domain.notification.dto.NotificationResponseDTO;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/history-like}")
    @Operation(summary = "기록에 좋아요를 누를 경우 상대방에개 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION", description = "기록 좋아요 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "좋아요가 눌러진 기록의 historyId, query parameter입니다.")
    })
    public BaseResponse<NotificationResponseDTO.HistoryLikeNotificationResult> likeHistoryNotification(@Parameter @Valid @HistoryExist Long historyId,
                                                                                    @Parameter(name = "user",hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.HistoryLikeNotificationResult result = notificationService.sendHistoryLikeNotification(member.getId(),historyId);

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_HISTORY_LIKED_SUCCESS, result);
    }

    @PostMapping("/new-follower}")
    @Operation(summary = "팔로우를 요청한 경우 상대방에게 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION", description = "팔로우 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "팔로우 API에 들어갔던 팔로우 하고자 하는 대상의 memberId (상대에게 팔로우 했다고 쏴줘야함)")
    })
    public BaseResponse<NotificationResponseDTO.NewFollowerNotificationResult> newFollowerNotification(@Parameter @Valid @IdValid String clokeyId,
                                                                                                       @Parameter(name = "user",hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.NewFollowerNotificationResult result = notificationService.sendNewFollowerNotification(clokeyId,member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_NEW_FOLLOWER_SUCCESS, result);
    }

    @PostMapping("/history-comment}")
    @Operation(summary = "기록에 좋아요를 누른 경우 기록의 주인에게 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION", description = "팔로우 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "댓글이 작성되는 기록의 Id"),
            @Parameter(name = "commentId", description = "작성된 댓글의 Id")
    })
    public BaseResponse<NotificationResponseDTO.HistoryCommentNotificationResult> historyCommentNotification(@Parameter @Valid @HistoryExist Long historyId,
                                                                                                             @Parameter @Valid @CommentExist Long commentId,
                                                                                                             @Parameter(name = "user",hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.HistoryCommentNotificationResult result = notificationService.sendHistoryCommentNotification(historyId,commentId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_HISTORY_COMMENT_SUCCESS, result);
    }

    @PostMapping("/comment-reply}")
    @Operation(summary = "댓글에 답글을 남기는 경우 댓글의 주인에게 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION", description = "댓글에 대한 답글 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "commentId", description = "답글 대상인 댓글의 Id"),
            @Parameter(name = "replyId", description = "작성된 답글의 Id")
    })
    public BaseResponse<NotificationResponseDTO.ReplyNotificationResult> replyNotification(@Parameter @Valid @CommentExist Long commentId,
                                                                                                    @Parameter @Valid @CommentExist Long replyId,
                                                                                                    @Parameter(name = "user",hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.ReplyNotificationResult result = notificationService.sendReplyNotification(commentId,replyId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_REPLY_SUCCESS, result);
    }


}
