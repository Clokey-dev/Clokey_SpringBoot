package com.clokey.server.domain.recommendation.api;

import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.history.dto.HistoryResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.domain.recommendation.application.RecommendationService;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;
import com.clokey.server.domain.recommendation.exception.annotation.CheckSection;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import com.clokey.server.global.error.exception.annotation.CheckPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class RecommendationController {
    private final RecommendationService recommendationService;

    @Operation(summary = "날씨에 맞는 옷 추천 API", description = "날씨에 맞는 옷 추천하는 API입니다.")
    @GetMapping("/recommend")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HOME_200", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<RecommendationResponseDTO.DailyClothesResult> recommendClothes(@Parameter(name = "user",hidden = true) @AuthUser Member member,
                                                                                   @RequestParam @Valid Float nowTemp) {
        RecommendationResponseDTO.DailyClothesResult response = recommendationService.getRecommendClothes(member.getId(), nowTemp);
        return BaseResponse.onSuccess(SuccessStatus.HOME_SUCCESS, response);
    }

    @Operation(summary = "홈 소식 내용 가져오기 API", description = "홈 소식 내용 가져오기 API입니다.")
    @GetMapping("/news")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HOME_200", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<RecommendationResponseDTO.DailyNewsResult> getNews(@Parameter(name = "user", hidden = true) @AuthUser Member member){
        RecommendationResponseDTO.DailyNewsResult response = recommendationService.getNews(member.getId());
        return BaseResponse.onSuccess(SuccessStatus.HOME_SUCCESS, response);
    }

    @Operation(summary = "홈 소식 내용 자세히 보기 API", description = "홈 소식 내용 자세히 보기 API입니다. closet, calendar만 가능합니다.")
    @GetMapping("/news/detail")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HOME_200", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<RecommendationResponseDTO.DailyNewsAllResult<?>> getNewsAll(@Parameter(name = "user", hidden = true) @AuthUser Member member,
                                                                           @RequestParam @Valid @CheckSection String section,
                                                                           @RequestParam @Valid @CheckPage Integer page) {
        RecommendationResponseDTO.DailyNewsAllResult<?> response = recommendationService.getNewsAll(member.getId(), section, page);
        return BaseResponse.onSuccess(SuccessStatus.HOME_SUCCESS, response);
    }

    @GetMapping(value = "/1-year-ago")
    @Operation(summary = "1년전 나 또는 팔로우의 기록을 확인하는 API")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "HOME_200", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<HistoryResponseDTO.LastYearHistoryResult> getLastYearHistory(@Parameter(name = "user", hidden = true) @AuthUser Member member) {
        HistoryResponseDTO.LastYearHistoryResult result = recommendationService.getLastYearHistory(member.getId());
        return BaseResponse.onSuccess(SuccessStatus.HOME_SUCCESS, result);
    }
}
