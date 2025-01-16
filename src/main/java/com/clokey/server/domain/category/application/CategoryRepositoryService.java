package com.clokey.server.domain.category.application;

import com.clokey.server.domain.model.entity.Category;

import java.util.List;

public interface CategoryRepositoryService {

    boolean categoryExist(Long categoryId);

    List<Category> getAllCategories();
}
