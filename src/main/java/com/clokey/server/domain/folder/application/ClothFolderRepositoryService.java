package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClothFolderRepositoryService {

    boolean existsByCloth_IdAndFolder_Id(Long clothId, Long folderId);

    void deleteAllByClothId(@Param("clothId") Long clothId);

    void saveAll(List<ClothFolder> clothFolder);
}
