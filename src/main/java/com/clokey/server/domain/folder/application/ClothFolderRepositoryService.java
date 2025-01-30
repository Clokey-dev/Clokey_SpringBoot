package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothFolderRepositoryService {

    boolean existsByClothIdAndFolderId(Long clothId, Long folderId);

    void deleteAllByClothId(@Param("clothId") Long clothId);

    void saveAll(List<ClothFolder> clothFolder);

    List<ClothFolder> existsByAllClothIdsAndFolderId(List<Long> clothIds, Long folderId);
}
