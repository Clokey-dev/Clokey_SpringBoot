package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.dto.CategoryResponseDTO;

public interface CategoryService {
    String createPrompt(String clothingName);
    String chatGPT(String prompt);
    CategoryResponseDTO.CategoryRecommendResult getChatGPTResponse(String clothingName);
}
