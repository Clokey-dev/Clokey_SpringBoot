package com.clokey.server.domain.history.api;

import com.clokey.server.domain.HashtagHistory.application.HashtagHistoryRepositoryService;
import com.clokey.server.domain.HistoryImage.application.HistoryImageRepositoryService;
import com.clokey.server.domain.MemberLike.application.MemberLikeRepositoryService;
import com.clokey.server.domain.history.application.HistoryConverter;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.dto.HistoryResponseDto;
import com.clokey.server.domain.model.History;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
public class HistoryRestController {

    private final HistoryImageRepositoryService historyImageRepositoryService;
    private final HistoryRepositoryService historyRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;
    private final MemberLikeRepositoryService memberLikeRepositoryService;

    //임시로 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    @GetMapping("/daily/{history_id}/{member_id}")
    @Operation(summary = "특정 일의 기록을 확인할 수 있는 API",description = "path variable로 history_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200",description = "OK, 성공적으로 조회되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_4002", description = "존재하지 않는 기록ID 입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "history_id", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDto.dayViewResult> getDaily(@PathVariable Long history_id, @PathVariable Long member_id) {

        List<String> imageUrl = historyImageRepositoryService.getHistoryImageUrls(history_id);
        List<String> hashtags = hashtagHistoryRepositoryService.getHistoryHashtags(history_id);
        int likeCount = memberLikeRepositoryService.countLikesOfHistory(history_id);
        boolean isLiked = memberLikeRepositoryService.memberLikedHistory(member_id,history_id);
        History history = historyRepositoryService.getHistoryById(history_id);

        return BaseResponse.onSucesss(SuccessStatus.HISTORY_SUCCESS,HistoryConverter.toDayViewResult(history,imageUrl,hashtags,likeCount,isLiked));
    }

    // 권한이 없는 경우를 추가해야함.
    //임시로 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    @GetMapping("/monthly/{this_member_id}/")
    @Operation(summary = "특정 멤버의 특정 월의 기록을 확인할 수 있는 API",description = "query parameter로 member_id와 month를 입력해주세요(YYYY-MM) 형태.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200",description = "성공적으로 조회되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_4002", description = "존재하지 않는 기록ID 입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "history_id", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDto.monthViewResult> getMonthlyHistories(@PathVariable Long this_member_id,
            @RequestParam(value = "member_id", required = false) Long memberId,
            @RequestParam(value = "month") String month) {

        //memberId가 null 이라면 this_member_id 즉 지금 이용자의 Id를 기준으로 찾습니다.
        Long searchMemberId = (memberId != null) ? memberId : this_member_id;

        List<History> histories = historyRepositoryService.getMemberHistoryByYearMonth(searchMemberId, month);
        List<String> historyImageUrls = historyRepositoryService.getFirstImageUrlsOfHistory(histories);

        return BaseResponse.onSucesss(SuccessStatus.HISTORY_SUCCESS,HistoryConverter.toMonthViewResult(searchMemberId,histories,historyImageUrls));
    }



}
