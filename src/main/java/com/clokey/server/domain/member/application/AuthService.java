package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.AuthDTO;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    // JWT 생성 관련 메서드들
    String generateAccessToken(Long userId, String email);
    String generateRefreshToken(Long userId);

    boolean validateJwtToken(String token);
    String extractEmailFromToken(String token);

    // 카카오 사용자 정보 조회 및 DB 저장 메서드 추가
    ResponseEntity<AuthDTO.TokenResponse> authenticateKakaoUser(String kakaoAccessToken);
    AuthDTO.KakaoUserResponse getUserInfoFromKakao(String kakaoAccessToken);

    public AuthDTO.TokenResponse refreshAccessToken(String refreshToken);


    AuthDTO.TokenResponse appleLogin(String code, String deviceToken, String refreshToken);

}
