package com.clokey.server.domain.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

public class ProfileResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileRPDTO {

        Long id;
        String bio;
        String email;
        String nickname;
        String clokeyId;
        String profileImageUrl;
        LocalDateTime updatedAt;
    }
}
