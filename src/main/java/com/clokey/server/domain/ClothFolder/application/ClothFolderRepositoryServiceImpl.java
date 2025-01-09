package com.clokey.server.domain.ClothFolder.application;

import com.clokey.server.domain.ClothFolder.dao.ClothFolderRepository;
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
