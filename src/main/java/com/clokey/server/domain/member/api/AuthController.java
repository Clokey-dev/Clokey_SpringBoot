package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.AppleAuthService;
import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.error.code.status.SuccessStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private AppleAuthService appleAuthService;

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
                AuthDTO.TokenResponse tokenResponse = appleAuthService.login(loginRequest.getAuthorizationCode());
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




//    @PostMapping("/login")
//    public ResponseEntity<BaseResponse<AuthDTO.TokenResponse>> login(@RequestBody AuthDTO.KakaoLoginRequest loginRequest) {
//        // 로그인 타입 확인
//        String loginType = loginRequest.getType();
//
//        if (loginType == null || loginType.isBlank()) {
//            throw new MemberException(ErrorStatus.MISSING_LOGIN_TYPE);
//        }
//
//        ResponseEntity<AuthDTO.TokenResponse> responseEntity;
//
//        if (loginType.equalsIgnoreCase("kakao")) {
//            // 카카오 로그인 처리
//            responseEntity = authService.authenticateKakaoUser(loginRequest.getAccessToken());
//        } else {
//            throw new MemberException(ErrorStatus.INVALID_LOGIN_TYPE);
//        }
//
//        // ResponseEntity에 BaseResponse 래핑해서 반환
//        SuccessStatus successStatus = (responseEntity.getStatusCode() == HttpStatus.CREATED)
//                ? SuccessStatus.LOGIN_CREATED
//                : SuccessStatus.LOGIN_SUCCESS;
//
//        return ResponseEntity.status(responseEntity.getStatusCode())
//                .body(BaseResponse.onSuccess(successStatus, responseEntity.getBody()));
//    }
//
//    @PostMapping("/login/apple")
//    public ResponseEntity<BaseResponse<AuthDTO.TokenResponse>> appleLogin(@RequestBody AuthDTO.AppleLoginRequest loginRequest) throws Exception {
//        // 로그인 타입 확인
//        String loginType = loginRequest.getType();
//
//        if (loginType == null || loginType.isBlank()) {
//            throw new MemberException(ErrorStatus.MISSING_LOGIN_TYPE);
//        }
//
//        ResponseEntity<AuthDTO.TokenResponse> responseEntity;
//
//        if (loginType.equalsIgnoreCase("apple")) {
//            // apple 로그인 처리
//            AuthDTO.TokenResponse tokenResponse = appleAuthService.login(loginRequest.getAuthorizationCode());
//            // ResponseEntity로 래핑해서 반환
//            responseEntity = ResponseEntity.status(HttpStatus.OK).body(tokenResponse);
//        } else {
//            throw new MemberException(ErrorStatus.LOGIN_FAILED);
//        }
//
//        // ResponseEntity에 BaseResponse 래핑해서 반환
//        SuccessStatus successStatus = (responseEntity.getStatusCode() == HttpStatus.CREATED)
//                ? SuccessStatus.LOGIN_CREATED
//                : SuccessStatus.LOGIN_SUCCESS;
//
//        return ResponseEntity.status(responseEntity.getStatusCode())
//                .body(BaseResponse.onSuccess(successStatus, responseEntity.getBody()));
//    }



    @PostMapping("/reissue-token")
    public BaseResponse<AuthDTO.TokenResponse> reissueToken(@RequestBody AuthDTO.RefreshTokenRequest request) {

        AuthDTO.TokenResponse response = authService.refreshAccessToken(request.getRefreshToken());
        return BaseResponse.onSuccess(SuccessStatus.LOGIN_UPDATED, response);

    }
}



