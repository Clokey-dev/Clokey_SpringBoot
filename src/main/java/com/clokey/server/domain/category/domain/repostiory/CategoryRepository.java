package com.clokey.server.domain.category.domain.repostiory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.clokey.server.domain.category.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
