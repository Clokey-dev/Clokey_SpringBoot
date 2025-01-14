package com.clokey.server.domain.category.converter;

import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.domain.model.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public CategoryResponseDTO convertToDTO(Category category) {
        return new CategoryResponseDTO(category.getId(), category.getName());
    }
}

