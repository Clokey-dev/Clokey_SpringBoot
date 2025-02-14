package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.dto.CategoryResponseDTO;

public interface CategoryService {
    CategoryResponseDTO.CategoryRecommendResult getChatGPTResponse(String clothingName);
}
