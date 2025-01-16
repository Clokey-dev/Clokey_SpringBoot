package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.mapping.ClothFolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClothFolderRepository extends JpaRepository<ClothFolder, Long> {

    boolean existsByCloth_IdAndFolder_Id(Long clothId, Long folderId);

}
