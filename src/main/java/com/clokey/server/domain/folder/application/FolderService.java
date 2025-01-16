package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.dto.FolderRequest;
import com.clokey.server.domain.model.entity.Folder;

public interface FolderService {
    Folder createFolder(Long memberId, FolderRequest.FolderCreateRequest request);
    void deleteFolder(Long folderId);
    boolean folderExist(Long folderId);
}
