package com.clokey.server.domain.ClothFolder.application;

public interface ClothFolderRepositoryService {

    boolean clothInFolder(Long clothId, Long folderId);

}
