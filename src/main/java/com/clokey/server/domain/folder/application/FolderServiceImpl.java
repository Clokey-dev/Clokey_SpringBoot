package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequest;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.model.Folder;
import com.clokey.server.domain.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {

    private final FolderRepositoryService folderRepositoryService;
    private final MemberRepositoryService memberRepositoryService;


    @Override
    @Transactional
    public Folder createFolder(Long memberId, FolderRequest.FolderCreateRequest request) {
        Member member = memberRepositoryService.getMember(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        Folder newFolder = FolderConverter.toFolder(request, member);
        folderRepositoryService.saveFolder(newFolder);
        return newFolder;
    }
}
