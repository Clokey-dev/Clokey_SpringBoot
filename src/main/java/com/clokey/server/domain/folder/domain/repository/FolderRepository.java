package com.clokey.server.domain.folder.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import com.clokey.server.domain.folder.domain.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Page<Folder> findAllByMemberId(Long memberId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Folder f WHERE f.id IN :folderIds")
    void deleteByFolderIds(@Param("folderIds") List<Long> folderIds);
}
