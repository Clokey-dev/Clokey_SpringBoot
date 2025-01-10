package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.model.Folder;

public interface FolderRepositoryService {

    boolean folderExist(Long folderId);
    void saveFolder(Folder folder);
}
