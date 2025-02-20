package com.clokey.server.domain.category.application;

import java.util.List;

import com.clokey.server.domain.category.dto.CategoryResponseDTO;

public interface CategoryService {
    CategoryResponseDTO.CategoryRecommendResult getChatGPTResponse(String clothingName);

    List<CategoryResponseDTO.CategoryRP> getAllCategories();
}
