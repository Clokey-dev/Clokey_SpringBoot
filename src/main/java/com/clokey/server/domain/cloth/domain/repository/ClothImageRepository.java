package com.clokey.server.domain.cloth.domain.repository;

import com.clokey.server.domain.cloth.domain.entity.ClothImage;
import com.clokey.server.domain.history.domain.entity.HistoryImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothImageRepository extends JpaRepository<ClothImage, Long> {

    @Transactional
    @Modifying
    int deleteByClothId(Long clothId);

    List<ClothImage> findByCloth_IdIn(List<Long> clothIds);
}
