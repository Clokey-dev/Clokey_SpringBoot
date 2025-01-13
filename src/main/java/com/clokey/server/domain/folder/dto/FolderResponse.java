package com.clokey.server.domain.folder.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FolderResponse {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FolderIdDTO {
        private Long folderId;
    }
}
