package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.dao.FolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FolderRepositoryImpl implements FolderRepositoryService{

    private final FolderRepository folderRepository;

    @Override
    public boolean folderExist(Long folderId) {
        return folderRepository.existsById(folderId);
    }
}
