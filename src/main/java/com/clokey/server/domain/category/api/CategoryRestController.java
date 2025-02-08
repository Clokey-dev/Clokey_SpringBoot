package com.clokey.server.domain.category.api;

import com.clokey.server.domain.category.application.CategoryQueryService;
import com.clokey.server.domain.category.application.CategoryService;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import com.clokey.server.global.error.exception.annotation.CheckPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryRestController {

    private final CategoryQueryService categoryQueryService;
    private final CategoryService categoryService;

    @Operation(summary = "카테고리 조회 API", description = "카테고리를 조회하는 API입니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryResponseDTO.CategoryRP>>> getAllCategories() {
        try {
            List<CategoryResponseDTO.CategoryRP> categories = categoryQueryService.getAllCategories();

            // 성공 시 커스텀 응답
            BaseResponse<List<CategoryResponseDTO.CategoryRP>> response = BaseResponse.onSuccess(
                    SuccessStatus.CATEGORY_SUCCESS, categories);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 실패 시 기본 응답 처리
            BaseResponse<List<CategoryResponseDTO.CategoryRP>> errorResponse = BaseResponse.onFailure(
                    "ERROR_500", "서버 오류가 발생했습니다.", null);
            return ResponseEntity.status(500)
                    .body(errorResponse);  // 기본 오류 상태 코드 500으로 응답
        }
    }

    @Operation(summary = "카테고리 추천 API", description = "ChatGPT API를 활용하여 카테고리를 추천해주는 API입니다.")
    @GetMapping("/recommend")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CATEGORY_200", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<CategoryResponseDTO.CategoryRecommendResult> getRecommendCategory(@Parameter(name = "user",hidden = true) @AuthUser Member member,
                                                                    @RequestParam(value = "name") @Valid String name) {
        CategoryResponseDTO.CategoryRecommendResult result = categoryService.getChatGPTResponse(name);
        return BaseResponse.onSuccess(SuccessStatus.CATEGORY_SUCCESS, result);
    }
}
