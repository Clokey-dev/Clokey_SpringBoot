package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.exception.FolderException;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.folder.domain.repository.FolderRepository;
import com.clokey.server.domain.member.domain.repository.MemberRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepositoryService folderRepositoryService;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public Folder createFolder(Long memberId, FolderRequestDTO.FolderCreateRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
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
}
