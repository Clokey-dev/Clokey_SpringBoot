package com.clokey.server.domain.category.converter;

import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.domain.model.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryConverter {

    public CategoryResponseDTO.CategoryRP convertToDTO(Category category) {
        return new CategoryResponseDTO.CategoryRP(category.getId(), category.getName());
    }
}

