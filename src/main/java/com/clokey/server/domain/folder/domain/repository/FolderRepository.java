package com.clokey.server.domain.folder.domain.repository;

import com.clokey.server.domain.folder.domain.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    Page<Folder> findAllByMemberId(Long memberId, Pageable pageable);
}
