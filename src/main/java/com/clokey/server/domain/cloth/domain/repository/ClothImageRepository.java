package com.clokey.server.domain.cloth.domain.repository;

import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long> {

    @Transactional
    @Modifying
    int deleteAllByClothId(Long clothId);
}
