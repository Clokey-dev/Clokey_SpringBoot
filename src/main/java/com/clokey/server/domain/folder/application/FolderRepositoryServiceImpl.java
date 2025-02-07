package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.folder.domain.repository.FolderRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderRepositoryServiceImpl implements FolderRepositoryService{

    private final FolderRepository folderRepository;

    @Transactional
    @Override
    public void save(Folder folderId) {
        folderRepository.save(folderId);
    }

    @Transactional
    @Override
    public void deleteById(Long folderId) {
        folderRepository.deleteById(folderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Folder findById(Long folderId) {
        return folderRepository.findById(folderId).orElseThrow(()-> new DatabaseException(ErrorStatus.NO_SUCH_FOLDER));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long folderId) {
        return folderRepository.existsById(folderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Folder> findAllByMemberId(Long memberId, Pageable page) {
        return folderRepository.findAllByMemberId(memberId, page);
    }
}
