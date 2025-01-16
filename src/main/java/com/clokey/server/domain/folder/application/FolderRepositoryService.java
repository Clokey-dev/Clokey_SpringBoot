package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.model.entity.Folder;

public interface FolderRepositoryService {

    boolean folderExist(Long folderId);
    void saveFolder(Folder folder);
    void deleteFolder(Long folderId);
}
