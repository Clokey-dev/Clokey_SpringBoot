package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.folder.exception.FolderException;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import com.clokey.server.domain.folder.exception.validator.FolderAccessibleValidator;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepositoryService folderRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final ClothFolderRepositoryService clothFolderRepositoryService;
    private final ClothRepositoryService clothRepositoryService;

    private final FolderAccessibleValidator folderAccessibleValidator;
    private final ClothAccessibleValidator clothAccessibleValidator;


    @Override
    @Transactional
    public Folder createFolder(Long memberId, FolderRequestDTO.FolderCreateRequest request) {
        Member member = memberRepositoryService.findMemberById(memberId);
        Folder newFolder = FolderConverter.toFolder(request, member);
        folderRepositoryService.save(newFolder);
        if(!request.getClothIds().isEmpty())
            addClothesToFolder(newFolder, request.getClothIds(), memberId);
        return newFolder;
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId, Long memberId) {
        folderAccessibleValidator.validateFolderAccessOfMember(folderId, memberId);
        try {
            folderRepositoryService.deleteById(folderId);
        } catch (Exception ex) {
            throw new FolderException(ErrorStatus.FAILED_TO_DELETE_FOLDER);
        }
    }

    @Override
    @Transactional
    public void editFolderName(Long folderId, String newName, Long memberId) {
        folderAccessibleValidator.validateFolderAccessOfMember(folderId, memberId);
        Folder folder = folderRepositoryService.findById(folderId);
        folder.rename(newName);
        folderRepositoryService.save(folder);
    }

    @Override
    @Transactional
    public void addClothesToFolder(Long folderId, FolderRequestDTO.UpdateClothesInFolderRequest request, Long memberId) {
        Folder folder = folderAccessibleValidator.validateFolderAccessOfMember(folderId, memberId);
        addClothesToFolder(folder, request.getClothIds(), memberId);
    }

    @Override
    @Transactional
    public void deleteClothesFromFolder(Long folderId, FolderRequestDTO.UpdateClothesInFolderRequest request, Long memberId) {
        Folder folder = folderAccessibleValidator.validateFolderAccessOfMember(folderId, memberId);

        List<Cloth> clothes = validateClothesExistAndAccessible(request.getClothIds(), memberId);

        List<ClothFolder> clothFolders = clothFolderRepositoryService.findAllByClothIdsAndFolderId(
                clothes.stream().map(Cloth::getId).collect(Collectors.toList()), folder.getId()
        );

        clothFolderRepositoryService.deleteAllByClothIdIn(clothFolders.stream().map(ClothFolder::getCloth).map(Cloth::getId).collect(Collectors.toList()));
    }

    @Override
    public FolderResponseDTO.FolderClothesResult getClothesFromFolder(Long folderId, Integer page, Long memberId){
        Folder folder = folderAccessibleValidator.validateFolderAccessOfMember(folderId, memberId);
        Pageable pageable = PageRequest.of(page-1, 12);
        Page<ClothFolder> clothFolders = clothFolderRepositoryService.findAllByFolderId(folder.getId(), pageable);
        return FolderConverter.toFolderClothesDTO(clothFolders);
    }

    @Override
    public FolderResponseDTO.FoldersResult getFolders(Integer page, Long memberId){
        Member member = memberRepositoryService.findMemberById(memberId);
        Pageable pageable = PageRequest.of(page-1, 12);
        Page<Folder> folders = folderRepositoryService.findAllByMemberId(member.getId(), pageable);
        List<Long> folderIds = folders.stream().map(Folder::getId).toList();
        Map<Long, String> folderImageMap = clothFolderRepositoryService.findClothImageUrlsFromFolderIds(folderIds);
        Map<Long, Long> itemCountMap = clothFolderRepositoryService.countClothesByFolderIds(folderIds);
        return FolderConverter.toFoldersDTO(folders, folderImageMap, itemCountMap);
    }


    private List<Cloth> validateClothesExistAndAccessible(List<Long> clothIds, Long memberId) {
        clothIds.forEach(clothId -> {
            if (!clothRepositoryService.existsById(clothId)) {
                throw new FolderException(ErrorStatus.NO_SUCH_CLOTH);
            }
        });

        List<Cloth> clothes = clothRepositoryService.findAllById(clothIds);

        clothAccessibleValidator.validateClothOfMember(
                clothes.stream().map(Cloth::getId).collect(Collectors.toList()), memberId
        );

        return clothes;
    }

    private void addClothesToFolder(Folder folder, List<Long> clothIds, Long memberId) {
        List<Cloth> clothes = validateClothesExistAndAccessible(clothIds, memberId);

        clothFolderRepositoryService.validateNoDuplicateClothes(clothes, folder.getId());

        List<ClothFolder> clothFolders = clothes.stream()
                .map(cloth -> new ClothFolder(cloth, folder))
                .collect(Collectors.toList());

        clothFolderRepositoryService.saveAll(clothFolders);
    }
}
