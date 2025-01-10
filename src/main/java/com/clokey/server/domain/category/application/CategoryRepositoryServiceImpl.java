package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.dao.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryRepositoryServiceImpl implements CategoryRepositoryService{

    private final CategoryRepository categoryRepository;

    @Override
    public boolean categoryExist(Long categoryId) {

        return categoryRepository.existsById(categoryId);

    }
}
