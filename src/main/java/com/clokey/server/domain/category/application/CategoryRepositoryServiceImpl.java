package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.converter.CategoryConverter;
import com.clokey.server.domain.category.dao.CategoryRepository;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.clokey.server.domain.model.Category;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryRepositoryServiceImpl implements CategoryRepositoryService{

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryConverter::convertToDTO)
                .collect(Collectors.toList());
    }
}
