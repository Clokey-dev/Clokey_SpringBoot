package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequest;
import com.clokey.server.domain.folder.exception.FolderDeleteException;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.entity.Folder;
import com.clokey.server.domain.model.entity.Member;
import com.clokey.server.domain.model.repository.FolderRepository;
import com.clokey.server.domain.model.repository.MemberRepository;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;


    @Override
    @Transactional
    public Folder createFolder(Long memberId, FolderRequest.FolderCreateRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
        Folder newFolder = FolderConverter.toFolder(request, member);
        folderRepository.save(newFolder);
        return newFolder;
    }

    @Override
    public void deleteFolder(Long folderId) {
        if(!folderExist(folderId)){
            throw new FolderDeleteException(ErrorStatus.NO_SUCH_FOLDER);
        }
        try {
            folderRepository.deleteById(folderId);
        } catch (Exception ex) {
            throw new FolderDeleteException(ErrorStatus.FAILED_TO_DELETE_FOLDER);
        }
    }

    @Override
    public boolean folderExist(Long folderId) {
        return folderRepository.existsById(folderId);
    }

}
