package com.clokey.server.domain.category.application;

import com.clokey.server.domain.model.repository.CategoryRepository;
import com.clokey.server.domain.model.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryRepositoryServiceImpl implements CategoryRepositoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean categoryExist(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
