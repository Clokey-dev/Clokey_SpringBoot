package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.folder.domain.repository.FolderRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FolderRepositoryServiceImpl implements FolderRepositoryService{

    private final FolderRepository folderRepository;

    @Override
    public void save(Folder folderId) {
        folderRepository.save(folderId);
    }

    @Override
    public void deleteById(Long folderId) {
        folderRepository.deleteById(folderId);
    }

    @Override
    public Folder findById(Long folderId) {
        return folderRepository.findById(folderId).orElseThrow(()-> new DatabaseException(ErrorStatus.NO_SUCH_FOLDER));
    }

    @Override
    public boolean existsById(Long folderId) {
        return folderRepository.existsById(folderId);
    }

    @Override
    public Page<Folder> findAllByMemberId(Long memberId, Pageable page) {
        return folderRepository.findAllByMemberId(memberId, page);
    }

    @Override
    public void deleteByFolderIds(List<Long> folderIds) {
        folderRepository.deleteByFolderIds(folderIds);
    }
}
