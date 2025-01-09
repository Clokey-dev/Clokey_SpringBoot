package com.clokey.server.domain.clothfolder.application;

public interface ClothFolderRepositoryService {

    boolean clothInFolder(Long clothId, Long folderId);

}
