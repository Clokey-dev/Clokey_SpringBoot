package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;

public interface FolderService {
    Folder createAndUpdateFolder(Long memberId, FolderRequestDTO.FolderCreateRequest request);
    void deleteFolder(Long folderId, Long memberId);
    FolderResponseDTO.FolderClothesResult getClothesFromFolder(Long folderId, Integer page, Long memberId);
    FolderResponseDTO.FoldersResult getFolders(Integer page, Long memberId);
}
