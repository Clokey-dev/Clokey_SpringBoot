package com.clokey.server.domain.folder.converter;

import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.model.entity.Folder;
import com.clokey.server.domain.model.entity.Member;


public class FolderConverter {
    public static Folder toFolder(FolderRequestDTO.FolderCreateRequest request, Member member) {
        return Folder.builder()
                .name(request.getFolderName())
                .member(member)
                .build();
    }

    public static FolderResponseDTO.FolderIdDTO toFolderIdDTO(Folder folder) {
        return FolderResponseDTO.FolderIdDTO.builder()
                .folderId(folder.getId())
                .build();
    }
}
