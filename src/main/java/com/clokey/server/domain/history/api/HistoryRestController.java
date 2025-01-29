package com.clokey.server.domain.history.api;

import com.clokey.server.domain.history.application.HistoryService;
import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.history.exception.annotation.CheckPage;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.domain.history.exception.annotation.HistoryImageQuantityLimit;
import com.clokey.server.domain.history.exception.annotation.MonthFormat;
import com.clokey.server.domain.member.exception.annotation.MemberExist;
import com.clokey.server.domain.member.exception.annotation.NullableMemberExist;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
@Validated
public class HistoryRestController {

    private final HistoryService historyService;

    //임시로 엔드 포인트 맨 뒤에 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    @GetMapping("/daily/{historyId}")
    @Operation(summary = "특정 유저의 특정 일의 기록을 확인할 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDTO.DailyHistoryResult> getDailyHistory(@PathVariable @Valid @HistoryExist Long historyId,
                                                                               @RequestParam Long myMemberId) {

        HistoryResponseDTO.DailyHistoryResult result = historyService.getDaily(historyId, myMemberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, result);
    }

    @GetMapping("/monthly")
    @Operation(summary = "특정 유저의 특정 월의 기록을 확인할 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "조회하고자 하는 memberId, 빈칸 입력시 현재 유저를 기준으로 합니다."),
            @Parameter(name = "month", description = "조회하고자 하는 월입니다. YYYY-MM 형식으로 입력해주세요. ex)2025-01")
    })
    public BaseResponse<HistoryResponseDTO.MonthViewResult> getMonthlyHistories(@RequestParam(value = "myMemberId") Long myMemberId,
                                                                                @RequestParam(value = "memberId", required = false) @Valid @NullableMemberExist Long memberId,
                                                                                @RequestParam(value = "month") @Valid @MonthFormat String month) {

        HistoryResponseDTO.MonthViewResult result = historyService.getMonthlyHistories(myMemberId, memberId, month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, result);

    }

    @GetMapping("/{historyId}/comments")
    @Operation(summary = "특정 기록의 댓글을 읽어올 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다.")
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다."),
            @Parameter(name = "page", description = "페이징 관련 query parameter")

    })
    public BaseResponse<HistoryResponseDTO.HistoryCommentResult> getComments(@PathVariable @Valid @HistoryExist Long historyId,
                                                                             @RequestParam(value = "page") @Valid @CheckPage int page) {
        //페이지를 1에서 부터 받기 위해서 -1을 해서 입력합니다.
        HistoryResponseDTO.HistoryCommentResult result = historyService.getComments(historyId, page - 1);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, result);
    }

    ;

    //사용자 토큰 받는 부분 추가해야함.
    @PostMapping("/like")
    @Operation(summary = "특정 기록에 좋아요를 누를 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "좋아요 상태가 성공적으로 변경되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.LikeResult> like(@RequestParam Long myMemberId,
                                                            @RequestBody @Valid HistoryRequestDTO.LikeStatusChange request) {

        //isLiked의 상태에 따라서 좋아요 -> 취소 , 좋아요가 없는 상태 -> 좋아요 로 바꿔주게 됩니다.
        HistoryResponseDTO.LikeResult result = historyService.changeLike(myMemberId, request.getHistoryId(), request.isLiked());

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_LIKE_STATUS_CHANGED, result);
    }

    @PostMapping("/{historyId}/comments")
    @Operation(summary = "댓글을 남길 수 있는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "성공적으로 댓글이 생성되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "댓글을 남기고자 하는 기록의 ID")
    })
    public BaseResponse<HistoryResponseDTO.CommentWriteResult> writeComments(@PathVariable @Valid @HistoryExist Long historyId,
                                                                             @RequestParam @Valid @MemberExist Long myMemberId,
                                                                             @RequestBody @Valid HistoryRequestDTO.CommentWrite request) {
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_CREATED, historyService.writeComment(historyId, request.getCommentId(), myMemberId, request.getContent()));

    }

    //임시로 토큰을 request param으로 받는중.
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 기록을 생성하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "CREATED, 성공적으로 생성되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.HistoryCreateResult> createHistory(
            @RequestPart("historyCreateRequest") @Valid HistoryRequestDTO.HistoryCreate historyCreateRequest,
            @RequestPart(value = "imageFile", required = false) @Valid @HistoryImageQuantityLimit List<MultipartFile> imageFiles,
            @RequestParam Long memberId
    ) {

        HistoryResponseDTO.HistoryCreateResult result = historyService.createHistory(historyCreateRequest, memberId, imageFiles);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_CREATED, result);
    }

    //임시로 토큰을 request param으로 받는중.
    @PatchMapping(value = "/{historyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "기록을 수정하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "성공적으로 수정되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "수정하고자 하는 기록의 Id입니다.")
    })
    public BaseResponse<Void> updateHistory(
            @RequestPart("historyUpdateRequest") @Valid HistoryRequestDTO.HistoryUpdate historyUpdate,
            @RequestPart(value = "imageFile", required = false) @Valid @HistoryImageQuantityLimit List<MultipartFile> imageFiles,
            @RequestParam Long myMemberId,
            @PathVariable Long historyId
    ) {

        historyService.updateHistory(historyUpdate, myMemberId, historyId, imageFiles);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_UPDATED, null);
    }

    @GetMapping(value = "/1-year-ago")
    @Operation(summary = "1년전 나의 기록을 확인하는 API", description = "따로 요구하는 값은 없습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_202", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.LastYearHistoryResult> getLastYearHistory(
            @RequestParam Long myMemberId
    ) {

        HistoryResponseDTO.LastYearHistoryResult result = historyService.getLastYearHistory(myMemberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, result);
    }


    @DeleteMapping(value = "/comments/{commentId}")
    @Operation(summary = "댓글을 삭제하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "댓글이 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "commentId", description = "삭제하고자 하는 댓글의 ID")
    })
    public BaseResponse<Void> deleteComment(
            @RequestParam Long memberId,
            @PathVariable Long commentId
    ) {

        historyService.deleteComment(commentId, memberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_DELETED, null);
    }

    //임시로 토큰을 request param으로 받는중.
    @PatchMapping(value = "/comments/{commentId}")
    @Operation(summary = "댓글을 수정하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "댓글이 성공적으로 수정되었습니다."),
    })
    @Parameters({
            @Parameter(name = "commentId", description = "수정하고자 하는 댓글의 ID")
    })
    public BaseResponse<Void> updateComment(
            @RequestBody @Valid HistoryRequestDTO.UpdateComment updateCommentRequest,
            @RequestParam Long myMemberId,
            @PathVariable Long commentId
    ) {

        historyService.updateComment(updateCommentRequest, commentId, myMemberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_UPDATED, null);
    }

    @DeleteMapping(value = "/{historyId}")
    @Operation(summary = "기록을 삭제하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "기록이 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "삭제하고자 하는 기록의 ID입니다.")
    })
    public BaseResponse<Void> deleteHistory(
            @RequestParam Long memberId,
            @PathVariable @HistoryExist Long historyId
    ) {

        historyService.deleteHistory(historyId, memberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED, null);
    }


}
