package com.clokey.server.domain.folder.domain.repository;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothFolderRepository extends JpaRepository<ClothFolder, Long> {

    boolean existsByClothIdAndFolderId(Long clothId, Long folderId);

    @Transactional
    @Modifying
    void deleteAllByClothId(@Param("clothId") Long clothId);

    List<ClothFolder> findByClothIdInAndFolderId(List<Long> clothIds, Long folderId);

    @Transactional
    void deleteAllByClothIdIn(List<Long> clothId);
}
