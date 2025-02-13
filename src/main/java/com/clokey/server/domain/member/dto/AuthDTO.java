package com.clokey.server.domain.member.dto;

import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AuthDTO {

    // KakaoUserResponseDTO 클래스
    @Data
    public static class KakaoUserResponse {

        private String id; // 사용자 고유 ID

        @JsonProperty("kakao_account")
        private KakaoAccount kakaoAccount;

        @Data
        public static class KakaoAccount {
            private String email; // 이메일
            private Profile profile;

            @Data
            public static class Profile {
                private String nickname; // 닉네임
            }
        }
    }

    @Data
    public static class LoginRequest{
        private String type; // 로그인 타입 (ex: "kakao", "apple")
        private String accessToken;
        private String authorizationCode;
        private String deviceToken;

    }


    @Data
    @AllArgsConstructor
    public static class TokenResponse {

        Long id;
        String email;
        String nickname;
        private String accessToken;
        private String refreshToken;
        RegisterStatus registerStatus;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshTokenRequest {
        private String refreshToken;
    }


    
}

