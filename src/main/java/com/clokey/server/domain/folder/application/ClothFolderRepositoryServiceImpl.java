package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import com.clokey.server.domain.folder.domain.repository.ClothFolderRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ClothFolderRepositoryServiceImpl implements ClothFolderRepositoryService{

    private final ClothFolderRepository clothFolderRepository;

    @Override
    public boolean existsByClothIdAndFolderId(Long clothId, Long folderId){
        return clothFolderRepository.existsByClothIdAndFolderId(clothId,folderId);
    }

    @Modifying
    @Transactional
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
    @Transactional
    public void deleteAllByClothIdIn(List<Long> clothIds) {
        clothFolderRepository.deleteAllByClothIdIn(clothIds);
    }

    @Override
    public void validateNoDuplicateClothes(List<Cloth> clothes, Long folderId) {
        List<Long> clothIds = clothes.stream().map(Cloth::getId).collect(Collectors.toList());
        List<ClothFolder> existingClothIds = clothFolderRepository.findByClothIdInAndFolderId(clothIds, folderId);

        if (!existingClothIds.isEmpty()) {
            throw new DatabaseException(ErrorStatus.CLOTH_ALREADY_IN_FOLDER);
        }
    }

    @Override
    public Page<ClothFolder> findAllByFolderId(Long folderId, Pageable page) {
        return clothFolderRepository.findAllByFolderId(folderId, page);
    }
}
