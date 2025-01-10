package com.clokey.server.domain.category.dao;

import com.clokey.server.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
