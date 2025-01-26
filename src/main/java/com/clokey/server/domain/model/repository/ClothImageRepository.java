package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.ClothImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long> {

    @Transactional
    @Modifying
    int deleteByClothId(Long clothId);
}
