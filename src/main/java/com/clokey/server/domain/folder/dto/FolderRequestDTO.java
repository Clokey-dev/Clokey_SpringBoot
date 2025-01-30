package com.clokey.server.domain.folder.dto;

import com.clokey.server.domain.cloth.exception.annotation.ClothExist;
import com.clokey.server.domain.folder.exception.annotation.FolderExist;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import java.util.List;

public class FolderRequestDTO {
    @Getter
    public static class FolderCreateRequest {
        @NotBlank(message = "폴더 이름은 필수 입력 값입니다.")
        String folderName;
    }

    @Getter
    public static class FolderEditRequest {
        @FolderExist
        Long folderId;
        @NotBlank(message = "newName은 필수 입력 값입니다.")
        String newName;
    }

    @Getter
    public static class AddClothesToFolderRequest {
        @FolderExist
        Long folderId;

        @NotEmpty(message = "clothesId은 필수 입력 값입니다.")
        List<Long> clothesId;
    }
}
