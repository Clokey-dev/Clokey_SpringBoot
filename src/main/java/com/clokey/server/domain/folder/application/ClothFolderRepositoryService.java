package com.clokey.server.domain.folder.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;

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
