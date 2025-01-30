package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.domain.entity.Folder;

import java.util.List;

public interface FolderService {
    Folder createFolder(Long memberId, FolderRequestDTO.FolderCreateRequest request);
    void deleteFolder(Long folderId, Long memberId);
    void editFolderName(Long folderId, String newName, Long memberId);
    void addClothesToFolder(FolderRequestDTO.AddClothesToFolderRequest request, Long memberId);
}
