package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.code.status.SuccessStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthDTO.TokenResponse>> login(@RequestBody AuthDTO.LoginRequest loginRequest) {
        // 로그인 타입 확인
        String loginType = loginRequest.getType();

        if (loginType == null || loginType.isBlank()) {
            throw new MemberException(ErrorStatus.MISSING_LOGIN_TYPE);
        }

        ResponseEntity<AuthDTO.TokenResponse> responseEntity;

            //카카오 로그인
            if (loginType.equalsIgnoreCase("kakao") && loginRequest.getAccessToken() != null) {
                responseEntity = authService.authenticateKakaoUser(loginRequest.getAccessToken());
            }
            //애플 로그인
            else if (loginType.equalsIgnoreCase("apple") && loginRequest.getAuthorizationCode() != null) {
                // Apple 로그인 처리
                AuthDTO.TokenResponse tokenResponse = authService.appleLogin(loginRequest.getAuthorizationCode(), loginRequest.getDeviceToken());
                responseEntity = ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
            }
            //로그인 타입이 잘못된 경우
            else if(!loginType.equalsIgnoreCase("kakao") && !loginType.equalsIgnoreCase("apple")) {
                throw new MemberException(ErrorStatus.INVALID_LOGIN_TYPE);
            }
            else{
                throw new MemberException(ErrorStatus.DUPLICATE_HASHTAGS);
            }

            SuccessStatus successStatus = (responseEntity.getStatusCode() == HttpStatus.CREATED)
                    ? SuccessStatus.LOGIN_CREATED
                    : SuccessStatus.LOGIN_SUCCESS;

            return ResponseEntity.status(responseEntity.getStatusCode())
                    .body(BaseResponse.onSuccess(successStatus, responseEntity.getBody()));


    }


    @PostMapping("/reissue-token")
    public BaseResponse<AuthDTO.TokenResponse> reissueToken(@RequestBody AuthDTO.RefreshTokenRequest request) {

        AuthDTO.TokenResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return BaseResponse.onSuccess(SuccessStatus.LOGIN_UPDATED, response);

    }
}



