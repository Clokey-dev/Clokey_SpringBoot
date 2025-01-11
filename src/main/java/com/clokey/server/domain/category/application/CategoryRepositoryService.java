package com.clokey.server.domain.category.application;

import com.clokey.server.domain.category.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryRepositoryService {

    List<CategoryResponseDTO> getAllCategories();}
