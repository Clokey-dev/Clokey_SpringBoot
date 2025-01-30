package com.clokey.server.domain.folder.api;

import com.clokey.server.domain.folder.application.FolderService;
import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequestDTO;
import com.clokey.server.domain.folder.dto.FolderResponseDTO;
import com.clokey.server.domain.folder.exception.annotation.FolderExist;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
public class FolderRestController {
    private final FolderService folderService;

    @Operation(summary = "폴더 생성 API", description = "폴더 생성하는 API입니다.")
    @PostMapping("/folders")
    public BaseResponse<FolderResponseDTO.FolderIdDTO> createFolder(@RequestParam Long memberId,
                                                                    @RequestBody FolderRequestDTO.FolderCreateRequest request) {
        FolderResponseDTO.FolderIdDTO response = FolderConverter.toFolderIdDTO(folderService.createFolder(memberId, request));
        return BaseResponse.onSuccess(SuccessStatus.FOLDER_CREATED, response);
    }

    @Operation(summary = "폴더 삭제 API", description = "폴더 삭제하는 API입니다.")
    @DeleteMapping("/folders/{folderId}")
    public BaseResponse<String> deleteFolder(@RequestParam Long memberId,
                                             @FolderExist @PathVariable Long folderId) {
        folderService.deleteFolder(folderId, memberId);
        return BaseResponse.onSuccess(SuccessStatus.FOLDER_DELETED, null);
    }

    @Operation(summary = "폴더 이름 수정 API", description = "폴더 이름 수정하는 API입니다.")
    @PatchMapping("/folders/{folderId}")
    public BaseResponse<String> editFolderName(@RequestParam Long memberId,
                                               @RequestBody FolderRequestDTO.FolderEditRequest request) {
        folderService.editFolderName(request.getFolderId(), request.getNewName(), memberId);
        return BaseResponse.onSuccess(SuccessStatus.FOLDER_EDIT_SUCCESS, null);
    }

    @Operation(summary = "폴더에 옷 추가 API", description = "폴더에 옷 추가하는 API입니다.")
    @PatchMapping("folders/{folderId}/clothes")
    public BaseResponse<String> addClothesToFolder(@RequestParam Long memberId,
                                                   @RequestBody FolderRequestDTO.AddClothesToFolderRequest request) {
        folderService.addClothesToFolder(request, memberId);
        return BaseResponse.onSuccess(SuccessStatus.FOLDER_ADD_CLOTHES_SUCCESS, null);
    }
}
