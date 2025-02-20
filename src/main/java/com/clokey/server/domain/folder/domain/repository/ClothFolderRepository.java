package com.clokey.server.domain.folder.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import jakarta.transaction.Transactional;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;

public interface ClothFolderRepository extends JpaRepository<ClothFolder, Long> {
    @Transactional
    @Modifying
    void deleteAllByClothId(@Param("clothId") Long clothId);

    @Transactional
    void deleteAllByClothIdInAndFolderId(List<Long> clothId, Long folderId);

    Page<ClothFolder> findAllByFolderId(Long folderId, Pageable page);

    @Query("SELECT cf.folder.id, c.image.imageUrl FROM ClothFolder cf JOIN cf.cloth c WHERE cf.folder.id IN :folderIds GROUP BY cf.folder.id")
    List<Object[]> findClothImageUrlsFromFolderIds(@Param("folderIds") List<Long> folderIds);

    @Transactional
    @Modifying
    void deleteAllByFolderId(@Param("folderId") Long folderId);

    @Transactional
    @Modifying
    @Query("DELETE FROM ClothFolder cf WHERE cf.cloth.id IN :clothIds")
    void deleteAllByClothIds(@Param("clothIds") List<Long> clothIds);

    @Transactional
    @Modifying
    @Query("DELETE FROM ClothFolder cf WHERE cf.folder.id IN :folderIds")
    void deleteAllByFolderIds(@Param("folderIds") List<Long> folderIds);

}
