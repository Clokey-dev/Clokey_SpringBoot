package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.exception.FolderException;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.folder.domain.repository.FolderRepository;
import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
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
    public void deleteFolder(Long folderId) {
        try {
            folderRepositoryService.deleteById(folderId);
        } catch (Exception ex) {
            throw new FolderException(ErrorStatus.FAILED_TO_DELETE_FOLDER);
        }
    }

    @Override
    @Transactional
    public void editFolderName(Long folderId, String newName) {
        Folder folder = folderRepositoryService.findById(folderId);
        folder.rename(newName);
        folderRepositoryService.save(folder);
    }

    @Override
    @Transactional
    public void addClothesToFolder(FolderRequestDTO.AddClothesToFolderRequest request) {
        Folder folder = folderRepositoryService.findById(request.getFolderId());

        List<Cloth> clothes = clothRepositoryService.findAllById(request.getClothesId());
        List<ClothFolder> clothFolders = clothes.stream()
                .map(cloth -> new ClothFolder(cloth, folder))
                .collect(Collectors.toList());

        clothFolderRepository.saveAll(clothFolders);
    }
}
