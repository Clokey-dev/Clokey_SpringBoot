package com.clokey.server.domain.folder.application;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface ClothFolderRepositoryService {

    boolean existsByCloth_IdAndFolder_Id(Long clothId, Long folderId);

    void deleteAllByClothId(@Param("clothId") Long clothId);
}
