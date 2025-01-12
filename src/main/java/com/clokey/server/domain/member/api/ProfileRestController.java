package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.dto.ProfileRequestDTO;
import com.clokey.server.domain.member.dto.ProfileResponseDTO;
import com.clokey.server.domain.member.application.ProfileCommandService;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("users/{user_id}/profile")
public class ProfileRestController {

    private final ProfileCommandService profileCommandService;

    @Operation(summary = "프로필 수정 API", description = "사용자의 프로필 정보를 수정하는 API입니다.")
    @PatchMapping
    public BaseResponse<ProfileResponseDTO.ProfileRPDTO> updateProfile(
            @PathVariable("user_id") Long userId,
            @RequestBody @Valid ProfileRequestDTO.ProfileRQDTO request) {

        ProfileResponseDTO.ProfileRPDTO response = profileCommandService.updateProfile(userId, request);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_SUCCESS, response);
    }
}

