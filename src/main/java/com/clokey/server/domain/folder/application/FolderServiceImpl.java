package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.exception.FolderException;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import com.clokey.server.domain.folder.exception.validator.FolderAccessibleValidator;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.global.error.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        Folder folder = folderRepositoryService.findById(folderId);
        folderAccessibleValidator.validateFolderAccessOfMember(folder.getId(), memberId);

        request.getClothIds().forEach(clothId -> {
            if(!clothRepositoryService.existsById(clothId)){
                throw new FolderException(ErrorStatus.NO_SUCH_CLOTH);
            }
        });

        List<Cloth> clothes = clothRepositoryService.findAllById(request.getClothIds());
        clothAccessibleValidator.validateClothOfMember(clothes.stream().map(Cloth::getId).collect(Collectors.toList()), memberId);

        // 이미 폴더에 있는 옷 ID 목록 조회
        List<ClothFolder> existingClothIds = clothFolderRepositoryService.existsByAllClothIdsAndFolderId(
                clothes.stream().map(Cloth::getId).collect(Collectors.toList()), folder.getId()
        );
        // 중복된 옷이 있으면 예외 발생
        if (!existingClothIds.isEmpty()) {
            throw new FolderException(ErrorStatus.CLOTH_ALREADY_IN_FOLDER);
        }

        List<ClothFolder> clothFolders = clothes.stream()
                .map(cloth -> new ClothFolder(cloth, folder))
                .collect(Collectors.toList());

        clothFolderRepositoryService.saveAll(clothFolders);
    }
}
