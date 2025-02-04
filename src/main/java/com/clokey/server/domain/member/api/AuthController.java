package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.AppleAuthService;
import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.models.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private AppleAuthService appleAuthService;

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

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthDTO.TokenResponse>> callback(HttpServletRequest request, HttpServletResponse response){
        AuthDTO.TokenResponse tokenResponse= appleAuthService.login(request.getParameter("code"),response);

        // 로그인 실패
        if (tokenResponse == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(BaseResponse.onFailure(ErrorStatus.LOGIN_FAILED, null));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.onSuccess(SuccessStatus.LOGIN_SUCCESS, tokenResponse));
        }
    }


    @PostMapping("/reissue-token")
    public BaseResponse<AuthDTO.TokenResponse> reissueToken(@RequestBody AuthDTO.RefreshTokenRequest request) {

        AuthDTO.TokenResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return BaseResponse.onSuccess(SuccessStatus.LOGIN_UPDATED, response);

    }
}



