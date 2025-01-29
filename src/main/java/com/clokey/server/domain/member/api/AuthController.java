package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.dto.MemberResponseDTO;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public BaseResponse<AuthDTO.TokenResponse> getKakaoUserInfo(
            @RequestBody AuthDTO.KakaoLoginRequest kakaoLoginRequest) {
            // AuthService를 통해 카카오 사용자 정보 조회 및 신규 가입 처리
            AuthDTO.TokenResponse response = authService.authenticateKakaoUser(kakaoLoginRequest.getAccessToken());
            return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_SUCCESS, response);
    }
}

