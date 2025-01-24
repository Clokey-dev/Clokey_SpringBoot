package com.clokey.server.domain.folder.application;

import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.exception.FolderException;
import com.clokey.server.domain.model.entity.Cloth;
import com.clokey.server.domain.model.entity.Folder;
import com.clokey.server.domain.model.entity.Member;
import com.clokey.server.domain.model.entity.mapping.ClothFolder;
import com.clokey.server.domain.model.repository.ClothFolderRepository;
import com.clokey.server.domain.model.repository.ClothRepository;
import com.clokey.server.domain.model.repository.FolderRepository;
import com.clokey.server.domain.model.repository.MemberRepository;
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

    private final FolderRepository folderRepository;
    private final MemberRepository memberRepository;
    private final ClothFolderRepository clothFolderRepository;
    private final ClothRepository clothRepository;


    @Override
    @Transactional
    public Folder createFolder(Long memberId, FolderRequestDTO.FolderCreateRequest request) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));
        Folder newFolder = FolderConverter.toFolder(request, member);
        folderRepository.save(newFolder);
        return newFolder;
    }

    @Override
    @Transactional
    public void deleteFolder(Long folderId) {
        try {
            folderRepository.deleteById(folderId);
        } catch (Exception ex) {
            throw new FolderException(ErrorStatus.FAILED_TO_DELETE_FOLDER);
        }
    }

    @Override
    @Transactional
    public void editFolderName(Long folderId, String newName) {
        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_FOLDER));
        folder.rename(newName);
        folderRepository.save(folder);
    }

    @Override
    @Transactional
    public void addClothesToFolder(FolderRequestDTO.AddClothesToFolderRequest request) {
        Folder folder = folderRepository.findById(request.getFolderId()).orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_FOLDER));

        List<Cloth> clothes = clothRepository.findAllById(request.getClothesId());
        List<ClothFolder> clothFolders = clothes.stream()
                .map(cloth -> new ClothFolder(cloth, folder))
                .collect(Collectors.toList());

        clothFolderRepository.saveAll(clothFolders);
    }
}
