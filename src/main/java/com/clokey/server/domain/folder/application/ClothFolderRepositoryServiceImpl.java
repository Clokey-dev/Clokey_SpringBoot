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
    @Transactional
    public void deleteAllByClothIdInAndFolderId(List<Long> clothIds, Long folderId) {
        clothFolderRepository.deleteAllByClothIdInAndFolderId(clothIds, folderId);
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

    @Modifying
    @Transactional
    public void deleteAllByFolderId(@Param("folderId") Long folderId){
        clothFolderRepository.deleteAllByFolderId(folderId);
    }

    @Override
    @Transactional
    public void deleteAllByClothIds(List<Long> clothIds) {
        clothFolderRepository.deleteAllByClothIds(clothIds);
    }

    @Override
    @Transactional
    public void deleteAllByFolderIds(List<Long> folderIds) {
        clothFolderRepository.deleteAllByFolderIds(folderIds);
    }
}
