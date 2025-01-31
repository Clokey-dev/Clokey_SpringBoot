package com.clokey.server.domain.member.dto;

import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
public class AuthDTO {

    // KakaoUserResponseDTO 클래스
    @Data
    public static class KakaoUserResponse {

        private Long id; // 사용자 고유 ID

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

    // KakaoLoginRequest 클래스
    @Data
    public static class KakaoLoginRequest {
        private String type; // 로그인 타입 (ex: "kakao", "apple")
        private String accessToken;
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
    public class RefreshTokenRequest {
        private String refreshToken;
    }

}

