package com.clokey.server.domain.folder.converter;

import com.clokey.server.domain.folder.dto.FolderRequest;
import com.clokey.server.domain.folder.dto.FolderResponse;
import com.clokey.server.domain.model.Folder;
import com.clokey.server.domain.model.Member;


public class FolderConverter {
    public static Folder toFolder(FolderRequest.FolderCreateRequest request, Member member) {
        return Folder.builder()
                .name(request.getFolderName())
                .member(member)
                .build();
    }

    public static FolderResponse.FolderIdDTO toFolderIdDTO(Folder folder) {
        return FolderResponse.FolderIdDTO.builder()
                .folderId(folder.getId())
                .build();
    }
}
