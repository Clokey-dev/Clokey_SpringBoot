package com.clokey.server.domain.category.domain.repostiory;

import com.clokey.server.domain.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
