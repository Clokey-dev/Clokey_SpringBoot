package com.clokey.server.domain.folder.dto;

import lombok.Getter;

public class FolderRequest {
    @Getter
    public static class FolderCreateRequest {
        String folderName;
    }
}
