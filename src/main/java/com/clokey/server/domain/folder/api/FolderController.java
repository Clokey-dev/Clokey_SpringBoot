package com.clokey.server.domain.folder.api;

import com.clokey.server.domain.folder.application.FolderService;
import com.clokey.server.domain.folder.converter.FolderConverter;
import com.clokey.server.domain.folder.dto.FolderRequest;
import com.clokey.server.domain.folder.dto.FolderResponse;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FolderController {
    private final FolderService folderService;

    @Operation(summary = "폴더 생성 API", description = "폴더 생성하는 API입니다.")
    @PostMapping("/folders")
    public BaseResponse<FolderResponse.FolderIdDTO> createFolder(@RequestParam Long memberId,
                                                       @RequestBody FolderRequest.FolderCreateRequest request) {
        FolderResponse.FolderIdDTO response = FolderConverter.toFolderIdDTO(folderService.createFolder(memberId, request));
        return BaseResponse.onSucesss(SuccessStatus.FOLDER_CREATED, response);
    }
}
