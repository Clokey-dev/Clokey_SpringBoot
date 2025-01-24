package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.Folder;

public interface FolderRepositoryService {

    void save(Folder folderId);

    void deleteById(Long folderId);

    Folder findById(Long folderId);

}
