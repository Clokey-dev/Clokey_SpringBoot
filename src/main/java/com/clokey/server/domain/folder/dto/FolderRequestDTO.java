package com.clokey.server.domain.folder.dto;

import java.util.Collections;
import java.util.List;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import lombok.Getter;

import com.clokey.server.domain.folder.exception.annotation.FolderExist;

public class FolderRequestDTO {
    @Getter
    public static class FolderCreateRequest {
        Long folderId = null;
        @NotBlank(message = "폴더 이름은 필수 입력 값입니다.")
        String folderName;
        List<Long> clothIds = Collections.emptyList();
    }

    @Getter
    public static class FolderEditRequest {
        @FolderExist
        Long folderId;
        @NotBlank(message = "newName은 필수 입력 값입니다.")
        String newName;
    }

    @Getter
    public static class UpdateClothesInFolderRequest {
        @NotEmpty(message = "clothIds은 필수 입력 값입니다.")
        List<Long> clothIds;
    }
}
