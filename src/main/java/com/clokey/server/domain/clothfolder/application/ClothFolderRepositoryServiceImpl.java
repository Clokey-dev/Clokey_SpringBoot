package com.clokey.server.domain.clothfolder.application;

import com.clokey.server.domain.clothfolder.dao.ClothFolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClothFolderRepositoryServiceImpl implements ClothFolderRepositoryService{

    private final ClothFolderRepository clothFolderRepository;

    @Override
    public boolean clothInFolder(Long clothId, Long folderId) {
        return clothFolderRepository.existsByCloth_IdAndFolder_Id(clothId,folderId);
    }
}
