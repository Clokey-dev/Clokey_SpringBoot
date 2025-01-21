package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.ClothImage;
import com.clokey.server.domain.model.entity.History;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long> {

    @Transactional
    @Modifying
    int deleteAllByClothId(Long clothId);
}
