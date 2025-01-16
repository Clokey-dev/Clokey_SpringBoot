package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.GetUserQueryService;
import com.clokey.server.domain.member.dto.MemberResponseDTO;
import com.clokey.server.domain.member.application.ProfileCommandService;
import com.clokey.server.domain.member.exception.annotation.IdExist;
import com.clokey.server.domain.member.exception.annotation.IdValid;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final ProfileCommandService profileCommandService;
    private final GetUserQueryService getUserQueryService;

    @Operation(summary = "프로필 수정 API", description = "사용자의 프로필 정보를 수정하는 API입니다.")
    @PatchMapping("users/{user_id}/profile")
    public BaseResponse<MemberResponseDTO.ProfileRP> updateProfile(
            @PathVariable("user_id") Long userId,
            @RequestBody @Valid MemberResponseDTO.ProfileRQ request) {

        MemberResponseDTO.ProfileRP response = profileCommandService.updateProfile(userId, request);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_SUCCESS, response);
    }

    @Operation(summary = "아이디 중복 조회 API", description = "사용자의 클로키 아이디가 이미 사용 중인지 조회하는 API입니다.")
    @GetMapping("users/{clokey_id}/check")
    public BaseResponse<Object> checkID(
            @IdExist @PathVariable("clokey_id") String clokeyId) { // 클로키 아이디를 PathVariable로 받음

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ID_SUCCESS, null);
    }


    @Operation(summary = "회원 조회 API", description = "다른 회원의 프로필을 조회하는 API입니다.")
    @GetMapping("users/{clokey_id}")
    public BaseResponse<Object> getUser(
            @IdValid @PathVariable("clokey_id") String clokeyId) {

        MemberResponseDTO.GetUserRP response = getUserQueryService.getUser(clokeyId);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_SUCCESS, response);
    }

}

