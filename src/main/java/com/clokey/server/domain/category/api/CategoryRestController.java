package com.clokey.server.domain.category.api;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.category.application.CategoryService;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 조회 API", description = "카테고리를 조회하는 API입니다.")
    @GetMapping
    public BaseResponse<List<CategoryResponseDTO.CategoryRP>> getAllCategories() {

        List<CategoryResponseDTO.CategoryRP> categories = categoryService.getAllCategories();

        return BaseResponse.onSuccess(SuccessStatus.CATEGORY_SUCCESS, categories);
    }

    @Operation(summary = "카테고리 추천 API", description = "ChatGPT API를 활용하여 카테고리를 추천해주는 API입니다.")
    @GetMapping("/recommend")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CATEGORY_200", description = "성공적으로 조회되었습니다."),
    })
    public BaseResponse<CategoryResponseDTO.CategoryRecommendResult> getRecommendCategory(@Parameter(name = "user", hidden = true) @AuthUser Member member,
                                                                                          @RequestParam(value = "name") @Valid String name) {
        CategoryResponseDTO.CategoryRecommendResult result = categoryService.getChatGPTResponse(name);
        return BaseResponse.onSuccess(SuccessStatus.CATEGORY_SUCCESS, result);
    }
}
