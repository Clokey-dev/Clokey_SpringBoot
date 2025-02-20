package com.clokey.server.domain.category.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

import com.clokey.server.domain.category.domain.entity.Category;
import com.clokey.server.domain.category.domain.repostiory.CategoryRepository;

@Transactional
@Service
@RequiredArgsConstructor
public class CategoryRepositoryServiceImpl implements CategoryRepositoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
