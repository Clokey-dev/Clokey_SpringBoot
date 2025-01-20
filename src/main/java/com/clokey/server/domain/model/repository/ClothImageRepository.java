package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.ClothImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long> {
}
