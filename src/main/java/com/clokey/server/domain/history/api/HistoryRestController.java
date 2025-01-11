package com.clokey.server.domain.history.api;

import com.clokey.server.domain.HashtagHistory.application.HashtagHistoryRepositoryService;
import com.clokey.server.domain.HistoryImage.application.HistoryImageRepositoryService;
import com.clokey.server.domain.MemberLike.application.MemberLikeRepositoryService;
import com.clokey.server.domain.history.converter.HistoryConverter;
import com.clokey.server.domain.history.application.HistoryRepositoryService;
import com.clokey.server.domain.history.dto.HistoryResponseDto;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.History;
import com.clokey.server.domain.model.enums.Visibility;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/histories")
public class HistoryRestController {

    private final HistoryImageRepositoryService historyImageRepositoryService;
    private final HistoryRepositoryService historyRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;
    private final MemberLikeRepositoryService memberLikeRepositoryService;
    private final MemberRepositoryService memberRepositoryService;

    //임시로 엔드 포인트 맨 뒤에 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    //이유는 isLiked를 확인해야 하기 때문입니다. ㅠ ㅠ
    @GetMapping("/daily/{historyId}/{memberId}")
    @Operation(summary = "특정 일의 기록을 확인할 수 있는 API",description = "path variable로 history_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200",description = "OK, 성공적으로 조회되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_4002", description = "존재하지 않는 기록ID 입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "historyId", description = "기록의 id, path variable 입니다.")
    })
    public BaseResponse<HistoryResponseDto.dayViewResult> getDaily(@PathVariable Long historyId, @PathVariable Long memberId) {

        //validation 임시 처리
        Optional<History> history = historyRepositoryService.getHistoryById(historyId);

        //존재하지 않는 history
        if (history.isEmpty()){
            return BaseResponse.onFailure(ErrorStatus.NO_SUCH_HISTORY,null);
        }

        //내가 올린 history가 아니고 비공개인 경우
        boolean isPrivate = history.get().getVisibility().equals(Visibility.PRIVATE);
        boolean isNotMyHistory = !history.get().getMember().getId().equals(memberId);

        if(isPrivate && isNotMyHistory) {
            return BaseResponse.onFailure(ErrorStatus.NO_PERMISSION_TO_ACCESS_HISTORY,null);
        }


        List<String> imageUrl = historyImageRepositoryService.getHistoryImageUrls(historyId);
        List<String> hashtags = hashtagHistoryRepositoryService.getHistoryHashtags(historyId);
        int likeCount = memberLikeRepositoryService.countLikesOfHistory(historyId);
        boolean isLiked = memberLikeRepositoryService.memberLikedHistory(memberId, historyId);

        return BaseResponse.onSucesss(SuccessStatus.HISTORY_SUCCESS,HistoryConverter.toDayViewResult(history.get(),imageUrl,hashtags,likeCount,isLiked));
    }

    // 권한이 없는 경우를 추가해야함. -> 월의 볼 수 있는 경우와 그렇지 않은 경우가 섞여있다면? 하... ㅠ
    //임시로 멤버 Id를 받도록 했습니다 토큰에서 나의 id를 가져올 수 있도록 수정해야함.
    @GetMapping("/monthly/{this_member_id}/")
    @Operation(summary = "특정 멤버의 특정 월의 기록을 확인할 수 있는 API",description = "query parameter로 member_id와 month를 입력해주세요(YYYY-MM) 형태.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_200",description = "성공적으로 조회되었습니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HISTORY_4002", description = "존재하지 않는 기록ID 입니다.",content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    @Parameters({
            @Parameter(name = "memberId", description = "조회하고자 하는 memberId, 빈칸 입력시 현재 유저를 기준으로 합니다."),
            @Parameter(name = "month", description = "조회하고자 하는 월입니다. YYYY-MM 형식으로 입력해주세요.")
    })
    public BaseResponse<HistoryResponseDto.monthViewResult> getMonthlyHistories(@PathVariable Long this_member_id,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "month") String month) {

        //memberId가 null 이라면 this_member_id 즉 지금 이용자의 Id를 기준으로 찾습니다.
        Long searchMemberId = (memberId != null) ? memberId : this_member_id;

        //임시 validation
        // 존재 하지 않는 멤버인 경우.
        if(!memberRepositoryService.memberExist(searchMemberId)) {
            return BaseResponse.onFailure(ErrorStatus.NO_SUCH_MEMBER,null);
        }


        List<History> histories = historyRepositoryService.getMemberHistoryByYearMonth(searchMemberId, month);
        List<String> historyImageUrls = historyRepositoryService.getFirstImageUrlsOfHistory(histories);

        return BaseResponse.onSucesss(SuccessStatus.HISTORY_SUCCESS,HistoryConverter.toMonthViewResult(searchMemberId,histories,historyImageUrls));
    }



}
