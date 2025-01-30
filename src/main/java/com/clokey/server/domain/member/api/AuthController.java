package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.dto.MemberResponseDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.code.status.SuccessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthDTO.TokenResponse>> login(@RequestBody AuthDTO.KakaoLoginRequest loginRequest) {
        // 로그인 타입 확인
        String loginType = loginRequest.getType();

        if (loginType == null || loginType.isBlank()) {
            throw new MemberException(ErrorStatus.MISSING_LOGIN_TYPE);
        }

        ResponseEntity<AuthDTO.TokenResponse> responseEntity;

        if (loginType.equalsIgnoreCase("kakao")) {
            // 카카오 로그인 처리
            responseEntity = authService.authenticateKakaoUser(loginRequest.getAccessToken());
        } else {
            throw new MemberException(ErrorStatus.INVALID_LOGIN_TYPE);
        }

        // ResponseEntity에 BaseResponse 래핑해서 반환
        SuccessStatus successStatus = (responseEntity.getStatusCode() == HttpStatus.CREATED)
                ? SuccessStatus.LOGIN_CREATED
                : SuccessStatus.LOGIN_SUCCESS;

        return ResponseEntity.status(responseEntity.getStatusCode())
                .body(BaseResponse.onSuccess(successStatus, responseEntity.getBody()));
    }
}



