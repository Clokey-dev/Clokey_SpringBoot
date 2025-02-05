package com.clokey.server.domain.folder.converter;

import com.clokey.server.domain.folder.domain.entity.ClothFolder;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;


public class FolderConverter {
    public static Folder toFolder(FolderRequestDTO.FolderCreateRequest request, Member member) {
        return Folder.builder()
                .name(request.getFolderName())
                .member(member)
                .build();
    }

    public static FolderResponseDTO.FolderId toFolderIdDTO(Folder folder) {
        return FolderResponseDTO.FolderId.builder()
                .folderId(folder.getId())
                .build();
    }

    public static FolderResponseDTO.FolderClothes toFolderClothesDTO(Page<ClothFolder> clothPage) {
        List<FolderResponseDTO.FolderCloth> clothes = clothPage.getContent().stream()
                .map(clothFolder -> new FolderResponseDTO.FolderCloth(
                        clothFolder.getCloth().getId(),
                        clothFolder.getCloth().getName(),
                        clothFolder.getCloth().getImage().getImageUrl(),
                        clothFolder.getCloth().getWearNum()
                ))
                .collect(Collectors.toList());

        return FolderResponseDTO.FolderClothes.builder()
                .clothes(clothes)
                .totalPage(clothPage.getTotalPages())
                .totalElements((int) clothPage.getTotalElements())
                .isFirst(clothPage.isFirst())
                .isLast(clothPage.isLast())
                .build();
    }
}
