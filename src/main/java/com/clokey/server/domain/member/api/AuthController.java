package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.domain.member.application.UnlinkService;
import com.clokey.server.domain.member.application.UnlinkServiceImpl;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.code.status.SuccessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UnlinkService logoutService;


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
            responseEntity = authService.authenticateKakaoUser(loginRequest.getAccessToken(), loginRequest.getDeviceToken());
        }
        //애플 로그인
        else if (loginType.equalsIgnoreCase("apple") && loginRequest.getAuthorizationCode() != null) {
            // Apple 로그인 처리
            AuthDTO.TokenResponse tokenResponse = authService.appleLogin(loginRequest.getAuthorizationCode(), loginRequest.getDeviceToken());
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
        }
        //로그인 타입이 잘못된 경우
        else if (!loginType.equalsIgnoreCase("kakao") && !loginType.equalsIgnoreCase("apple")) {
            throw new MemberException(ErrorStatus.INVALID_LOGIN_TYPE);
        } else {
            throw new MemberException(ErrorStatus.DUPLICATE_HASHTAGS);
        }

        SuccessStatus successStatus = (responseEntity.getStatusCode() == HttpStatus.CREATED) ? SuccessStatus.LOGIN_CREATED : SuccessStatus.LOGIN_SUCCESS;

        return ResponseEntity.status(responseEntity.getStatusCode()).body(BaseResponse.onSuccess(successStatus, responseEntity.getBody()));


    }


    @Operation(summary = "토큰 재발급 API", description = "액세스 토큰을 재발급하는 API입니다.")
    @PostMapping("/reissue-token")
    public BaseResponse<AuthDTO.TokenResponse> reissueToken(@RequestBody AuthDTO.RefreshTokenRequest request) {

        AuthDTO.TokenResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return BaseResponse.onSuccess(SuccessStatus.LOGIN_UPDATED, response);

    }


    @Operation(summary = "회원탈퇴 API", description = "회원탈퇴하는 API입니다.")
    @DeleteMapping("/unlink")
    public BaseResponse<Object> unlink(@Parameter(name = "user", hidden = true) @AuthUser Member member) {
        System.out.println("memberId : " + member.getId());
        logoutService.unlink(member.getId());
        return BaseResponse.onSuccess(SuccessStatus.UNLINK_SUCCESS, null);
    }


}



