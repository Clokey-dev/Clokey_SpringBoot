package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ClothFolderRepositoryService {

    void deleteAllByClothId(@Param("clothId") Long clothId);

    void saveAll(List<ClothFolder> clothFolder);

    void deleteAllByClothIdInAndFolderId(List<Long> clothIds, Long folderId);

    Page<ClothFolder> findAllByFolderId(Long folderId, Pageable page);

    Map<Long, String> findClothImageUrlsFromFolderIds(List<Long> folderIds);

    void deleteAllByFolderId(@Param("folderId") Long folderId);

    void deleteAllByClothIds(List<Long> clothIds);

    void deleteAllByFolderIds(List<Long> folderIds);
}
