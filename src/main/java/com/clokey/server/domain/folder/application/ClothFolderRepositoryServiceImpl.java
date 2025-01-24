package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.repository.ClothFolderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

@Transactional
@Service
@RequiredArgsConstructor
public class ClothFolderRepositoryServiceImpl implements ClothFolderRepositoryService{

    private final ClothFolderRepository clothFolderRepository;

    @Override
    public boolean existsByCloth_IdAndFolder_Id(Long clothId, Long folderId){
        return clothFolderRepository.existsByCloth_IdAndFolder_Id(clothId,folderId);
    }

    @Modifying
    public void deleteAllByClothId(@Param("clothId") Long clothId){
        clothFolderRepository.deleteAllByClothId(clothId);
    }
}
