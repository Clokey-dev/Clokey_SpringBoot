package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.AuthDTO;

public interface AuthService {

    // JWT 생성 관련 메서드들
    String generateJwtToken(Long userId, String email);
    boolean validateJwtToken(String token);
    String extractEmailFromToken(String token);

    // 카카오 사용자 정보 조회 및 DB 저장 메서드 추가
    AuthDTO.KakaoUserResponse getKakaoUserInfo(String accessToken);
}
