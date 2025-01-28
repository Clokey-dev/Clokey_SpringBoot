package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.domain.entity.Category;

import java.util.List;

public interface CategoryRepositoryService {

    boolean existsById(Long id);

    List<Category> findAll();
}
