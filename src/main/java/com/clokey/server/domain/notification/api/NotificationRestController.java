package com.clokey.server.domain.notification.api;


import com.clokey.server.domain.history.exception.annotation.CommentExist;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.domain.member.exception.annotation.IdValid;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.domain.notification.application.NotificationService;
import com.clokey.server.domain.notification.dto.NotificationResponseDTO;
import com.clokey.server.domain.notification.exception.annotation.NotificationExist;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import com.clokey.server.global.error.exception.annotation.CheckPage;
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
@RequestMapping("/notifications")
@Validated
public class NotificationRestController {

    private final NotificationService notificationService;

    @GetMapping("")
    @Operation(summary = "알림을 조회하는 API", description = "알림을 조회하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_200", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<NotificationResponseDTO.GetNotificationResult> getNotifications(@Parameter(name = "user", hidden = true) @AuthUser Member member,
                                                                                        @RequestParam(value = "page") @CheckPage @Valid Integer page) {

        NotificationResponseDTO.GetNotificationResult result = notificationService.getNotifications(member.getId(), page);

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_SUCCESS, result);
    }

    @GetMapping("/not-read-exist")
    @Operation(summary = "안 읽은 알림 여부를 조회하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_200", description = "읽지 않은 알림 여부가 성공적으로 조회되었습니다."),
    })
    public BaseResponse<NotificationResponseDTO.UnReadNotificationCheckResult> checkUnReadNotifications(@Parameter(name = "user", hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.UnReadNotificationCheckResult result = notificationService.checkUnReadNotifications(member.getId());

        return BaseResponse.onSuccess(SuccessStatus.UNREAD_NOTIFICATION_CHECKED, result);
    }

    @PatchMapping("/{notificationId}/read")
    @Operation(summary = "알림을 읽음 처리 할 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_204", description = "알림이 성공적으로 읽음 처리되었습니다."),
    })
    @Parameters({
            @Parameter(name = "notificationId", description = "읽음 처리를 하고자 하는 기록의 Id")
    })
    public BaseResponse<Void> readNotification(@PathVariable @Valid @NotificationExist Long notificationId,
                                               @Parameter(name = "user", hidden = true) @AuthUser Member member) {

        notificationService.readNotification(notificationId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_READ, null);
    }

    @PatchMapping
    @Operation(summary = "모든 알림을 읽음 처리 할 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_204", description = "알림이 성공적으로 읽음 처리되었습니다."),
    })
    public BaseResponse<Void> readAllNotification(@Parameter(name = "user", hidden = true) @AuthUser Member member) {

        notificationService.readAllNotification(member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_READ, null);
    }

    @PostMapping("/history-like")
    @Operation(summary = "기록에 좋아요를 누를 경우 상대방에개 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_200", description = "기록 좋아요 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "좋아요가 눌러진 기록의 historyId, query parameter입니다.")
    })
    public BaseResponse<NotificationResponseDTO.HistoryLikeNotificationResult> likeHistoryNotification(@Parameter @Valid @HistoryExist Long historyId,
                                                                                                       @Parameter(name = "user", hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.HistoryLikeNotificationResult result = notificationService.sendHistoryLikeNotification(member.getId(), historyId);

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_HISTORY_LIKED_SUCCESS, result);
    }

    @PostMapping("/new-follower")
    @Operation(summary = "팔로우를 요청한 경우 상대방에게 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_200", description = "팔로우 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clokeyId", description = "팔로우 API에 들어갔던 팔로우 하고자 하는 대상의 memberId (상대에게 팔로우 했다고 쏴줘야함)")
    })
    public BaseResponse<NotificationResponseDTO.NewFollowerNotificationResult> newFollowerNotification(@Parameter @Valid @IdValid String clokeyId,
                                                                                                       @Parameter(name = "user", hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.NewFollowerNotificationResult result = notificationService.sendNewFollowerNotification(clokeyId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_NEW_FOLLOWER_SUCCESS, result);
    }

    @PostMapping("/history-comment")
    @Operation(summary = "기록에 댓글을 남기는 경우 기록의 주인에게 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_200", description = "팔로우 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "댓글이 작성되는 기록의 Id"),
            @Parameter(name = "commentId", description = "작성된 댓글의 Id")
    })
    public BaseResponse<NotificationResponseDTO.HistoryCommentNotificationResult> historyCommentNotification(@Parameter @Valid @HistoryExist Long historyId,
                                                                                                             @Parameter @Valid @CommentExist Long commentId,
                                                                                                             @Parameter(name = "user", hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.HistoryCommentNotificationResult result = notificationService.sendHistoryCommentNotification(historyId, commentId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_HISTORY_COMMENT_SUCCESS, result);
    }

    @PostMapping("/comment-reply")
    @Operation(summary = "댓글에 답글을 남기는 경우 댓글의 주인에게 알림을 보내는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_200", description = "댓글에 대한 답글 알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "commentId", description = "답글 대상인 댓글의 Id"),
            @Parameter(name = "replyId", description = "작성된 답글의 Id")
    })
    public BaseResponse<NotificationResponseDTO.ReplyNotificationResult> replyNotification(@Parameter @Valid @CommentExist Long commentId,
                                                                                           @Parameter @Valid @CommentExist Long replyId,
                                                                                           @Parameter(name = "user", hidden = true) @AuthUser Member member) {

        NotificationResponseDTO.ReplyNotificationResult result = notificationService.sendReplyNotification(commentId, replyId, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_REPLY_SUCCESS, result);
    }

    @PostMapping("/1-year-ago")
    @Operation(summary = "1년전 오늘의 소식을 확인하라는 알림을 발송하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_204", description = "알림이 성공적으로 발송되었습니다."),
    })
    public BaseResponse<Void> sendOneYearAgoNotification(@Parameter(name = "user", hidden = true) @AuthUser Member member) {

        notificationService.sendOneYearAgoNotification(member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_SEND_SUCCESS, null);
    }

    @PostMapping("/today-temperature")
    @Operation(summary = "오늘의 온도에 대한 알림을 발송하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_204", description = "알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "temperatureDiff", description = "어제와의 온도차이를 정수로 입력해주세요")
    })
    public BaseResponse<Void> sendTodayTemperatureNotification(@Parameter @Valid Integer temperatureDiff,
                                                               @Parameter(name = "user", hidden = true) @AuthUser Member member) {

        notificationService.sendTodayTemperatureNotification(temperatureDiff, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_SEND_SUCCESS, null);
    }

    @PostMapping("/seasons")
    @Operation(summary = "계절 변화에 대한 알림을 발송하는 API")
    @ApiResponses({

            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "NOTIFICATION_204", description = "알림이 성공적으로 발송되었습니다."),
    })
    @Parameters({
            @Parameter(name = "season", description = "계절에 맞는 알림을 선택합니다.")
    })
    public BaseResponse<Void> sendSeasonsNotification(@Parameter @Valid Season season,
                                                      @Parameter(name = "user", hidden = true) @AuthUser Member member) {

        notificationService.sendSeasonsNotification(season, member.getId());

        return BaseResponse.onSuccess(SuccessStatus.NOTIFICATION_SEND_SUCCESS, null);
    }


}
