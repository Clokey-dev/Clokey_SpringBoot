package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.converter.CategoryConverter;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.domain.model.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    @Override
    public boolean categoryExist(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    public List<CategoryResponseDTO.CategoryRP> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryConverter::convertToDTO)
                .collect(Collectors.toList());
    }
}
