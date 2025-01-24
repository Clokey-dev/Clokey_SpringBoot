package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.folder.domain.repository.FolderRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
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
}
