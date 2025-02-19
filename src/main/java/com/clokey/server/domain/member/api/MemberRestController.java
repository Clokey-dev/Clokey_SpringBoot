package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.MemberService;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.domain.member.exception.annotation.IdValid;
import com.clokey.server.domain.member.exception.annotation.*;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @Operation(summary = "로그아웃 API", description = "사용자의 access token과 refresh token을 날려줍니다.")
    @PostMapping(value = "users/logout")
    public BaseResponse<Void> logout(
            @Parameter(name = "user", hidden = true) @AuthUser Member member){

        memberService.logout(member.getId());

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_EDITED, null);
    }

    @Operation(summary = "프로필 수정 API", description = "사용자의 프로필 정보를 수정하는 API입니다.")
    @PatchMapping(value = "users/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<MemberDTO.ProfileRP> updateProfile(
            @Parameter(name = "user", hidden = true) @AuthUser Member member,
            @RequestPart("profileRequest") @Valid MemberDTO.ProfileRQ request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "profileBackImage", required = false) MultipartFile profileBackImage) {

        MemberDTO.ProfileRP response = memberService.updateProfile(member.getId(), request, profileImage, profileBackImage);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_EDITED, response);
    }


    @Operation(summary = "아이디 중복 조회 API", description = "사용자의 클로키 아이디가 이미 사용 중인지 조회하는 API입니다.")
    @GetMapping("users/{clokey_id}/check")
    public BaseResponse<Void> checkID(
            @Parameter(name = "user", hidden = true) @AuthUser Member currentUser,
            @PathVariable("clokey_id") String clokeyId) { // 클로키 아이디를 PathVariable로 받음

        memberService.clokeyIdUsingCheck(clokeyId, currentUser);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ID_SUCCESS, null);
    }


    @Operation(summary = "회원 조회 API", description = "다른 회원의 프로필을 조회하는 API입니다.")
    @GetMapping("users")
    public BaseResponse<Object> getUser(
            @Parameter(name = "user", hidden = true) @AuthUser Member currentUser, // 현재 로그인한 사용자 추가
            @NullableClokeyIdExist @RequestParam(value = "clokey_id", required = false) String clokeyId) {


        MemberDTO.GetUserRP response = memberService.getUser(clokeyId, currentUser);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_SUCCESS, response);
    }



    @Operation(summary = "팔로우 조회 API", description = "내가 다른 사용자를 팔로우하고있는지 확인하는 API입니다.")
    @PostMapping("users/follow/check/{clokey_id}")
    public BaseResponse<MemberDTO.FollowRP> followCheck(
            @Parameter(name = "user", hidden = true) @AuthUser Member currentUser,
            @IdValid @PathVariable("clokey_id") String clokeyId){

        MemberDTO.FollowRP response= memberService.followCheck(clokeyId, currentUser);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_SUCCESS, response);
    }



    @Operation(summary = "팔로우 API", description = "다른 사용자를 팔로우/언팔로우하는 API입니다. 호출시마다 기존 상태와 반대로 변경됩니다.")
        @PostMapping("users/follow/{clokey_id}")
    public BaseResponse<Void> follow(
            @Parameter(name = "user", hidden = true) @AuthUser Member currentUser,
            @IdValid @PathVariable("clokey_id") String clokeyId) {

        memberService.follow(clokeyId, currentUser);

        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_EDITED, null);
    }


    @Operation(summary = "팔로잉/팔로워 목록 조회 API", description = "팔로잉/팔로워 목록을 조회하는 api입니다.")
    @GetMapping("users/{clokeyId}/follow")
    public BaseResponse<MemberDTO.GetFollowMemberResult> getFollowMembers(@Parameter(name = "user", hidden = true) @AuthUser Member member,
                                                                          @PathVariable("clokeyId") String clokeyId,
                                                                          @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                                          @RequestParam(value = "isFollowing", defaultValue = "true") Boolean isFollowing){
        MemberDTO.GetFollowMemberResult response = memberService.getFollowPeople(member.getId(), clokeyId, page, isFollowing);
        return BaseResponse.onSuccess(SuccessStatus.MEMBER_SUCCESS, response);
    }
}

