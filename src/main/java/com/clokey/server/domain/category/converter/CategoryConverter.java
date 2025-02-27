package com.clokey.server.domain.category.converter;

import org.springframework.stereotype.Component;

import com.clokey.server.domain.category.domain.entity.Category;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;

@Component
public class CategoryConverter {
    public CategoryResponseDTO.CategoryRP convertToDTO(Category category) {
        return new CategoryResponseDTO.CategoryRP(category.getId(), category.getName());
    }

    public static CategoryResponseDTO.CategoryRecommendResult toRecommendResultDTO(Integer categoryId, String largeCategoryName, String smallCategoryName) {
        return new CategoryResponseDTO.CategoryRecommendResult(categoryId, largeCategoryName, smallCategoryName);
    }
}
