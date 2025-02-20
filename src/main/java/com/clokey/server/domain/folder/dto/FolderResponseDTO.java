package com.clokey.server.domain.folder.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FolderResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FolderIdResult {
        private Long folderId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FolderClothesResult {
        private List<FolderClothResult> clothes;
        private Integer totalPage;
        private Integer totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Getter
    @AllArgsConstructor
    public static class FolderClothResult {
        private Long clothId;
        private String clothName;
        private String imageUrl;
        private Integer clothCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FoldersResult {
        private List<FolderResult> folders;
        private Integer totalPage;
        private Integer totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Getter
    @AllArgsConstructor
    public static class FolderResult {
        private Long folderId;
        private String folderName;
        private String imageUrl;
        private Long itemCount;
    }
}
