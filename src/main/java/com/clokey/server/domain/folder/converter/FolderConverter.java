package com.clokey.server.domain.folder.converter;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class FolderConverter {
    public static Folder toFolder(FolderRequestDTO.FolderCreateRequest request, Member member) {
        return Folder.builder()
                .name(request.getFolderName())
                .member(member)
                .build();
    }

    public static FolderResponseDTO.FolderIdResult toFolderIdDTO(Folder folder) {
        return FolderResponseDTO.FolderIdResult.builder()
                .folderId(folder.getId())
                .build();
    }

    public static FolderResponseDTO.FolderClothesResult toFolderClothesDTO(Page<ClothFolder> clothPage) {
        List<FolderResponseDTO.FolderClothResult> clothes = clothPage.getContent().stream()
                .map(clothFolder -> new FolderResponseDTO.FolderClothResult(
                        clothFolder.getCloth().getId(),
                        clothFolder.getCloth().getName(),
                        clothFolder.getCloth().getImage().getImageUrl(),
                        clothFolder.getCloth().getWearNum()
                ))
                .collect(Collectors.toList());

        return FolderResponseDTO.FolderClothesResult.builder()
                .clothes(clothes)
                .totalPage(clothPage.getTotalPages())
                .totalElements((int) clothPage.getTotalElements())
                .isFirst(clothPage.isFirst())
                .isLast(clothPage.isLast())
                .build();
    }

    public static FolderResponseDTO.FoldersResult toFoldersDTO(Page<Folder> folderPage, Map<Long, String> folderImageMap, Map<Long, Long> itemCountMap) {
        List<FolderResponseDTO.FolderResult> folders = folderPage.getContent().stream()
                .map(folder -> new FolderResponseDTO.FolderResult(
                        folder.getId(),
                        folder.getName(),
                        folderImageMap.getOrDefault(folder.getId(), null),
                        itemCountMap.getOrDefault(folder.getId(), 0L)
                ))
                .collect(Collectors.toList());

        return FolderResponseDTO.FoldersResult.builder()
                .folders(folders)
                .totalPage(folderPage.getTotalPages())
                .totalElements((int) folderPage.getTotalElements())
                .isFirst(folderPage.isFirst())
                .isLast(folderPage.isLast())
                .build();
    }
}
