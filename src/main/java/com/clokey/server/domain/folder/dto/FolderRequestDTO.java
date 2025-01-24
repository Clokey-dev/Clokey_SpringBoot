package com.clokey.server.domain.folder.dto;

import com.clokey.server.domain.folder.exception.annotation.FolderExist;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@NotNull
public class FolderRequestDTO {
    @Getter
    public static class FolderCreateRequest {
        String folderName;
    }

    @Getter
    public static class FolderEditRequest {
        @FolderExist
        Long folderId;
        String newName;
    }

    @Getter
    public static class AddClothesToFolderRequest {
        @FolderExist
        Long folderId;
        List<Long> clothesId;
    }
}
