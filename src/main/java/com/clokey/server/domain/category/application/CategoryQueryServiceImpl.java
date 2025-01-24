package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.converter.CategoryConverter;
import com.clokey.server.domain.category.dto.CategoryResponseDTO;
import com.clokey.server.domain.category.domain.repostiory.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryQueryServiceImpl implements CategoryQueryService {

    private final CategoryRepositoryService categoryRepositoryService;
    private final CategoryConverter categoryConverter;

    @Override
    public boolean categoryExist(Long categoryId) {
        return categoryRepositoryService.existsById(categoryId);
    }

    @Override
    public List<CategoryResponseDTO.CategoryRP> getAllCategories() {
        return categoryRepositoryService.findAll()
                .stream()
                .map(categoryConverter::convertToDTO)
                .collect(Collectors.toList());
    }
}
