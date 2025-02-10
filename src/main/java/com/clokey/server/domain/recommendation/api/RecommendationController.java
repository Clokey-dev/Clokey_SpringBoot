package com.clokey.server.domain.recommendation.api;

import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.domain.recommendation.application.RecommendationService;
import com.clokey.server.domain.recommendation.dto.RecommendationResponseDTO;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
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
    public BaseResponse<RecommendationResponseDTO.DailyNewsResult> getNews(@Parameter(name = "user",hidden = true) @AuthUser Member member,
                                                                              @RequestParam @Valid String view,
                                                                              @RequestParam(required = false) String section,
                                                                              @RequestParam(required = false) Integer page) {
        RecommendationResponseDTO.DailyNewsResult response = recommendationService.getIssues(member.getId(), view, section, page);
        return BaseResponse.onSuccess(SuccessStatus.HOME_SUCCESS, response);
    }
}
