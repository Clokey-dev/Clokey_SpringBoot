package com.clokey.server.domain.history.api;

import com.clokey.server.domain.history.application.HistoryService;
import com.clokey.server.domain.history.dto.HistoryRequestDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.history.exception.annotation.CheckPage;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.domain.history.exception.annotation.HistoryImageQuantityLimit;
import com.clokey.server.domain.history.exception.annotation.MonthFormat;
import com.clokey.server.domain.history.exception.validator.CommentValidator;
import com.clokey.server.domain.history.exception.validator.HistoryAccessibleValidator;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.exception.annotation.MemberExist;
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

    private final HistoryLikedValidator historyLikedValidator;
    private final HistoryAccessibleValidator historyAccessibleValidator;
    private final HistoryService historyService;
    private final CommentValidator commentValidator;

    //임시로 엔드 포인트 맨 뒤에 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    @GetMapping("/daily/{historyId}/{memberId}")
    @Operation(summary = "특정 일의 기록을 확인할 수 있는 API", description = "path variable로 history_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDTO.DayViewResult> getDaily(@PathVariable @Valid @HistoryExist Long historyId, @PathVariable Long memberId) {

        historyAccessibleValidator.validateHistoryAccessOfMember(historyId, memberId);

        HistoryResponseDTO.DayViewResult result = historyService.getDaily(historyId, memberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, result);
    }

    //임시로 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    //결국 자신의 기록을 보는지 확인하기 위해 MemberId 쿼리 파라미터는 필수적으로 받아야합니다.
    @GetMapping("/monthly")
    @Operation(summary = "특정 멤버의 특정 월의 기록을 확인할 수 있는 API", description = "query parameter로 member_id와 month를 입력해주세요(YYYY-MM) 형태.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "조회하고자 하는 memberId, 빈칸 입력시 현재 유저를 기준으로 합니다."),
            @Parameter(name = "month", description = "조회하고자 하는 월입니다. YYYY-MM 형식으로 입력해주세요.")
    })

    public BaseResponse<HistoryResponseDTO.MonthViewResult> getMonthlyHistories(@RequestParam(value = "thisMemberId") Long thisMemberId,
                                                                                @RequestParam(value = "memberId", required = false) @Valid @MemberExist Long memberId,
                                                                                @RequestParam(value = "month") @Valid @MonthFormat String month) {

        HistoryResponseDTO.MonthViewResult result = historyService.getMonthlyHistories(thisMemberId, memberId, month);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, result);

    }

    @GetMapping("/{historyId}/comments")
    @Operation(summary = "특정 기록의 댓글을 읽어올 수 있는 API", description = "쿼리 파라미터로 페이지를 넘겨주세요.")
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
    @PostMapping("/like/{thisMemberId}")
    @Operation(summary = "특정 기록에 좋아요를 누를 수 있는 API", description = "history의 ID와 isLiked 정보를 body에 넣어서 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "좋아요 상태가 성공적으로 변경되었습니다."),
    })
    @Parameters({
            @Parameter(name = "thisMemberId", description = "현재 유저의 ID 토큰 대체용입니다.")
    })
    public BaseResponse<HistoryResponseDTO.LikeResult> like(@PathVariable Long thisMemberId,
                                                            @RequestBody @Valid HistoryRequestDTO.LikeStatusChange request) {

        //isLiked 정보가 정확한지 검증합니다.
        historyLikedValidator.validateIsLiked(thisMemberId, request.getHistoryId(), request.isLiked());

        //isLiked의 상태에 따라서 좋아요 -> 취소 , 좋아요가 없는 상태 -> 좋아요 로 바꿔주게 됩니다.
        HistoryResponseDTO.LikeResult result = historyService.changeLike(thisMemberId, request.getHistoryId(), request.isLiked());

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_LIKE_STATUS_CHANGED, result);
    }

    @PostMapping("/histories/{historyId}/comments/{thisMemberId}")
    @Operation(summary = "댓글을 남길 수 있는 API", description = "History_id를 path parameter로 넘겨주세요. Body에는 내용과 대댓글 대상 댓글 Id(Optional)을 받습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_201", description = "성공적으로 댓글이 생성되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.CommentWriteResult> writeComments(@PathVariable @Valid @HistoryExist Long historyId,
                                                                             @PathVariable @Valid @MemberExist Long thisMemberId,
                                                                             @RequestBody @Valid HistoryRequestDTO.CommentWrite request) {
        commentValidator.validateParentCommentHistory(historyId, request.getCommentId());
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_CREATED, historyService.writeComment(historyId, request.getCommentId(), thisMemberId, request.getContent()));

    }

    //임시로 토큰을 request param으로 받는중.
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 기록을 생성하는 API", description = "request body에 HistoryRequestDTO.HistoryCreate 형식의 데이터를 전달해주세요.")
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
    @Operation(summary = "기록을 수정하는 API", description = "request body에 HistoryRequestDTO.HistoryUpdate 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "성공적으로 수정되었습니다."),
    })
    public BaseResponse<Void> updateHistory(
            @RequestPart("historyUpdateRequest") @Valid HistoryRequestDTO.HistoryUpdate historyUpdate,
            @RequestPart(value = "imageFile", required = false) @Valid @HistoryImageQuantityLimit List<MultipartFile> imageFiles,
            @RequestParam Long memberId,
            @PathVariable Long historyId
    ) {

        historyService.updateHistory(historyUpdate, memberId, historyId, imageFiles);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_UPDATED,null);
    }

    @GetMapping(value = "/1-year-ago")
    @Operation(summary = "1년전 나의 기록을 확인하는 API", description = "따로 요구하는 값은 없습니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_202", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.LastYearHistoryResult> viewLastYearHistory(
            @RequestParam Long memberId
    ) {

        HistoryResponseDTO.LastYearHistoryResult result = historyService.getLastYearHistory(memberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS,result);
    }


    @DeleteMapping(value = "/comments/{commentId}")
    @Operation(summary = "댓글을 삭제하는 API", description = "Path Parameter로 Comment Id를 입력해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "댓글이 성공적으로 삭제되었습니다."),
    })
    public BaseResponse<Void> deleteComment(
            @RequestParam Long memberId,
            @PathVariable Long commentId
    ) {

        historyService.deleteComment(commentId,memberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_DELETED,null);
    }

    //임시로 토큰을 request param으로 받는중.
    @PatchMapping(value = "/comments/{commentId}")
    @Operation(summary = "댓글을 수정하는 API", description = "Path Parameter로 Comment Id를 입력해주세요, HistoryRequestDTO.UpdateComment에는 수정되는 내용을 담아주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "댓글이 성공적으로 수정되었습니다."),
    })
    public BaseResponse<Void> updateComment(
            @RequestBody @Valid HistoryRequestDTO.UpdateComment updateCommentRequest,
            @RequestParam Long memberId,
            @PathVariable Long commentId
    ) {

        historyService.updateComment(updateCommentRequest,commentId,memberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_COMMENT_UPDATED,null);
    }

    @DeleteMapping(value = "/{historyId}")
    @Operation(summary = "기록을 삭제하는 API", description = "Path Parameter로 history Id를 입력해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_204", description = "기록이 성공적으로 삭제되었습니다."),
    })
    public BaseResponse<Void> deleteHistory(
            @RequestParam Long memberId,
            @PathVariable @HistoryExist Long historyId
    ) {

        historyService.deleteHistory(historyId,memberId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_DELETED,null);
    }


}
