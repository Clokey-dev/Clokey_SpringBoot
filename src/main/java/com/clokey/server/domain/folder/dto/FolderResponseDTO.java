package com.clokey.server.domain.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class FolderResponseDTO {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FolderId {
        private Long folderId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FolderClothes {
        private List<FolderCloth> clothes;
        private Integer totalPage;
        private Integer totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Getter
    @AllArgsConstructor
    public static class FolderCloth {
        private Long clothId;
        private String clothName;
        private String imageUrl;
        private Integer clothCount;
    }
}
