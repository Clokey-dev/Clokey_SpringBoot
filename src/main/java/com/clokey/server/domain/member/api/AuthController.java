package com.clokey.server.domain.member.api;

import com.clokey.server.domain.member.application.AuthService;
import com.clokey.server.domain.member.dto.AuthDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<?> getKakaoUserInfo(@RequestBody AuthDTO.KakaoLoginRequest kakaoLoginRequest) {
        try {
            // AuthService를 통해 카카오 사용자 정보 조회 및 신규 가입 처리
            AuthDTO.KakaoUserResponse kakaoUser = authService.getKakaoUserInfo(kakaoLoginRequest.getAccessToken());
            return ResponseEntity.ok(kakaoUser); // 사용자 정보를 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }
    }
}
