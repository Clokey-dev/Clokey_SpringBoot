package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import com.clokey.server.domain.folder.domain.repository.ClothFolderRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ClothFolderRepositoryServiceImpl implements ClothFolderRepositoryService{

    private final ClothFolderRepository clothFolderRepository;

    @Override
    public boolean existsByClothIdAndFolderId(Long clothId, Long folderId){
        return clothFolderRepository.existsByClothIdAndFolderId(clothId,folderId);
    }

    @Modifying
    public void deleteAllByClothId(@Param("clothId") Long clothId){
        clothFolderRepository.deleteAllByClothId(clothId);
    }

    @Override
    @Transactional
    public void saveAll(List<ClothFolder> clothFolder) {
        clothFolderRepository.saveAll(clothFolder);
    }


    @Override
    public List<ClothFolder> findAllByClothIdsAndFolderId(List<Long> clothIds, Long folderId) {
        return clothFolderRepository.findByClothIdInAndFolderId(clothIds, folderId);
    }

    @Override
    public void deleteAllByClothIdIn(List<Long> clothIds) {
        clothFolderRepository.deleteAllByClothIdIn(clothIds);
    }
}
