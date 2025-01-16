package com.clokey.server.domain.member.dto;

import com.clokey.server.domain.member.exception.annotation.EssentialFieldNotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProfileRequestDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileRQ {

        @EssentialFieldNotNull
        private String nickname;

        @EssentialFieldNotNull
        private String clokeyId;

        private String profileImageUrl;

        private String bio;
    }

}
