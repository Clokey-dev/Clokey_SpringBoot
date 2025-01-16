package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.model.repository.FolderRepository;
import com.clokey.server.domain.model.entity.Folder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderRepositoryServiceImpl implements FolderRepositoryService{

    @Lazy
    private final FolderRepository folderRepository;

    @Override
    public boolean folderExist(Long folderId) {
        return folderRepository.existsById(folderId);
    }


    @Override
    public void saveFolder(Folder folder) {
        folderRepository.save(folder);
    }

    @Override
    public void deleteFolder(Long folderId) {
        folderRepository.deleteById(folderId);
    }

}
