package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.Folder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FolderRepositoryService {

    void save(Folder folderId);

    void deleteById(Long folderId);

    Folder findById(Long folderId);

    boolean existsById(Long folderId);

    Page<Folder> findAllByMemberId(Long memberId, Pageable page);

    void deleteByFolderIds(List<Long> folderIds);
}
