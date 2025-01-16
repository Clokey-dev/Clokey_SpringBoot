package com.clokey.server.domain.category.api;

import com.clokey.server.domain.category.application.CategoryQueryService;
import com.clokey.server.domain.category.application.CategoryRepositoryService;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryRestController {

    private final CategoryQueryService categoryService;

    public CategoryRestController(CategoryQueryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "카테고리 조회 API", description = "카테고리를 조회하는 API입니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryResponseDTO.CategoryRP>>> getAllCategories() {
        try {
            List<CategoryResponseDTO.CategoryRP> categories = categoryService.getAllCategories();

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
}
