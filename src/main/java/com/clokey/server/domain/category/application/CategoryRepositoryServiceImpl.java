package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.domain.entity.Category;
import com.clokey.server.domain.category.domain.repostiory.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
