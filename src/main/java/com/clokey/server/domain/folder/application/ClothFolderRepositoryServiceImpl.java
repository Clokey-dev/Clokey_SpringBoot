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
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ClothFolderRepositoryServiceImpl implements ClothFolderRepositoryService{

    private final ClothFolderRepository clothFolderRepository;

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public List<ClothFolder> findAllByClothIdsAndFolderId(List<Long> clothIds, Long folderId) {
        return clothFolderRepository.findByClothIdInAndFolderId(clothIds, folderId);
    }

    @Override
    @Transactional
    public void deleteAllByClothIdIn(List<Long> clothIds) {
        clothFolderRepository.deleteAllByClothIdIn(clothIds);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateNoDuplicateClothes(List<Cloth> clothes, Long folderId) {
        List<Long> clothIds = clothes.stream().map(Cloth::getId).collect(Collectors.toList());
        List<ClothFolder> existingClothIds = clothFolderRepository.findByClothIdInAndFolderId(clothIds, folderId);
        if (!existingClothIds.isEmpty()) {
            throw new DatabaseException(ErrorStatus.CLOTH_ALREADY_IN_FOLDER);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClothFolder> findAllByFolderId(Long folderId, Pageable page) {
        return clothFolderRepository.findAllByFolderId(folderId, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, String> findClothImageUrlsFromFolderIds(List<Long> folderIds) {
        List<Object[]> results = clothFolderRepository.findClothImageUrlsFromFolderIds(folderIds);
        return results.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (String) row[1]));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Long> countClothesByFolderIds(List<Long> folderIds) {
        List<Object[]> results = clothFolderRepository.countClothesByFolderIds(folderIds);
        return results.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));
    }

    @Modifying
    @Transactional
    public void deleteAllByFolderId(@Param("folderId") Long folderId){
        clothFolderRepository.deleteAllByFolderId(folderId);
    }

    @Override
    public Long countByFolderId(Long folderId) {
        return clothFolderRepository.countByFolderId(folderId);
    }
}
