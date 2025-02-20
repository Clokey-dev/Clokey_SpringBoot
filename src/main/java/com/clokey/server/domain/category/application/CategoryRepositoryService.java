package com.clokey.server.domain.category.application;

import java.util.List;

import com.clokey.server.domain.category.domain.entity.Category;

public interface CategoryRepositoryService {

    boolean existsById(Long id);

    List<Category> findAll();
}
