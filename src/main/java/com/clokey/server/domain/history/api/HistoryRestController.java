package com.clokey.server.domain.history.api;

import com.clokey.server.domain.HashtagHistory.application.HashtagHistoryRepositoryService;
import com.clokey.server.domain.HistoryImage.application.HistoryImageRepositoryService;
import com.clokey.server.domain.MemberLike.application.MemberLikeRepositoryService;
import com.clokey.server.domain.history.application.HistoryService;
import com.clokey.server.domain.comment.application.CommentRepositoryService;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.dto.HistoryRequestDto;
import com.clokey.server.domain.history.dto.HistoryResponseDto;
import com.clokey.server.domain.history.exception.annotation.CheckPage;
import com.clokey.server.domain.history.exception.annotation.HistoryExist;
import com.clokey.server.domain.history.exception.annotation.MonthFormat;
import com.clokey.server.domain.history.exception.validator.HistoryAccessibleValidator;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.exception.annotation.MemberExist;
import com.clokey.server.domain.model.Comment;
import com.clokey.server.domain.model.History;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
@Validated
public class HistoryRestController {

    private final HistoryImageRepositoryService historyImageRepositoryService;
    private final HistoryRepositoryService historyRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;
    private final MemberLikeRepositoryService memberLikeRepositoryService;
    private final HistoryLikedValidator historyLikedValidator;
    private final HistoryAccessibleValidator historyAccessibleValidator;
    private final HistoryService historyService;
    private final CommentRepositoryService commentRepositoryService;

    //임시로 엔드 포인트 맨 뒤에 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    @GetMapping("/daily/{historyId}/{memberId}")
    @Operation(summary = "특정 일의 기록을 확인할 수 있는 API", description = "path variable로 history_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDto.DayViewResult> getDaily(@PathVariable @Valid @HistoryExist Long historyId, @PathVariable Long memberId) {

        //멤버가 기록에 대해서 접근 권한이 있는지 확인합니다.
        historyAccessibleValidator.validateHistoryAccessOfMember(historyId, memberId);

        Optional<History> history = historyRepositoryService.getHistoryById(historyId);
        List<String> imageUrl = historyImageRepositoryService.getHistoryImageUrls(historyId);
        List<String> hashtags = hashtagHistoryRepositoryService.getHistoryHashtags(historyId);
        int likeCount = memberLikeRepositoryService.countLikesOfHistory(historyId);
        boolean isLiked = memberLikeRepositoryService.memberLikedHistory(memberId, historyId);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, HistoryConverter.toDayViewResult(history.get(), imageUrl, hashtags, likeCount, isLiked));
    }

    //임시로 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    //결국 자신의 기록을 보는지 확인하기 위해 MemberId 쿼리 파라미터는 필수적으로 받아야합니다.
    @GetMapping("/monthly/{this_member_id}/")
    @Operation(summary = "특정 멤버의 특정 월의 기록을 확인할 수 있는 API", description = "query parameter로 member_id와 month를 입력해주세요(YYYY-MM) 형태.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200", description = "성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "조회하고자 하는 memberId, 빈칸 입력시 현재 유저를 기준으로 합니다."),
            @Parameter(name = "month", description = "조회하고자 하는 월입니다. YYYY-MM 형식으로 입력해주세요.")
    })

    public BaseResponse<HistoryResponseDto.MonthViewResult> getMonthlyHistories(@PathVariable Long this_member_id,
                                                                                @RequestParam(value = "memberId") @Valid @MemberExist Long memberId,
                                                                                @RequestParam(value = "month") @Valid @MonthFormat String month) {

        //멤버 자체에 대한 접근 권한 확인.
        historyAccessibleValidator.validateMemberAccessOfMember(memberId, this_member_id);

        List<History> histories = historyRepositoryService.getMemberHistoryByYearMonth(memberId, month);
        List<String> historyImageUrls = historyRepositoryService.getFirstImageUrlsOfHistory(histories);

        //나의 기록 열람은 공개 범위와 상관없이 모두 열람 가능합니다.

        if (this_member_id.equals(memberId)) {
            return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, HistoryConverter.toAllMonthViewResult(memberId, histories, historyImageUrls));
        }

        //다른 멤버 기록 열람시 PUBLIC 기록만을 모아줍니다.
        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, HistoryConverter.toPublicMonthViewResult(memberId, histories, historyImageUrls));

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
    public BaseResponse<HistoryResponseDto.HistoryCommentResult> getComments(@PathVariable @Valid @HistoryExist Long historyId,
                                                                             @RequestParam(value = "page") @Valid @CheckPage int page) {
        //페이지를 1에서 부터 받기 위해서 -1을 해서 입력합니다.
        Page<Comment> comments = commentRepositoryService.getNoneReplyCommentsByHistoryId(historyId, page - 1);
        List<List<Comment>> repliesForEachComment = commentRepositoryService.getReplyListOfCommentList(comments);

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_SUCCESS, HistoryConverter.toHistoryCommentResult(comments, repliesForEachComment));
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
    public BaseResponse<HistoryResponseDto.LikeResult> like(@PathVariable Long thisMemberId,
                                                            @RequestBody @Valid HistoryRequestDto.likeStatusChange request) {

        //isLiked 정보가 정확한지 검증합니다.
        historyLikedValidator.validateIsLiked(thisMemberId, request.getHistoryId(), request.isLiked());

        //isLiked의 상태에 따라서 좋아요 -> 취소 , 좋아요가 없는 상태 -> 좋아요 로 바꿔주게 됩니다.
        historyService.changeLike(thisMemberId, request.getHistoryId(), request.isLiked());

        return BaseResponse.onSuccess(SuccessStatus.HISTORY_LIKE_STATUS_CHANGED, HistoryConverter.toLikeResult(
                historyRepositoryService.getHistoryById(request.getHistoryId()).get(),
                request.isLiked()
        ));
    }

}