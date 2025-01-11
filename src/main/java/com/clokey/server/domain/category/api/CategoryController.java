package com.clokey.server.domain.category.api;

import com.clokey.server.domain.category.application.CategoryRepositoryService;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.BaseErrorCode;
import com.clokey.server.global.error.code.status.SuccessStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryRepositoryService categoryService;

    public CategoryController(CategoryRepositoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<List<CategoryResponseDTO>>> getAllCategories() {
        try {
            List<CategoryResponseDTO> categories = categoryService.getAllCategories();

            // 성공 시 커스텀 응답
            BaseResponse<List<CategoryResponseDTO>> response = BaseResponse.onSuccess(
                    SuccessStatus.CATEGORY_SUCCESS, categories);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 실패 시 기본 응답 처리
            BaseResponse<List<CategoryResponseDTO>> errorResponse = BaseResponse.onFailure(
                    "ERROR_500", "서버 오류가 발생했습니다.", null);
            return ResponseEntity.status(500)
                    .body(errorResponse);  // 기본 오류 상태 코드 500으로 응답
        }
    }
}
