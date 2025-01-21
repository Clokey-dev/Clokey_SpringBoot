package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.model.entity.Folder;

public interface FolderService {
    Folder createFolder(Long memberId, FolderRequestDTO.FolderCreateRequest request);
    void deleteFolder(Long folderId);
    void editFolderName(Long folderId, String newName);
}
