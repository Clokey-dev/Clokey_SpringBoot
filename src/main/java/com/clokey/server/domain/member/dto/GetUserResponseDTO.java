package com.clokey.server.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GetUserResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GetUserRP {

        String clokeyId;
        String profileImageUrl;
        Long recordCount;
        Long followerCount;
        Long followingCount;
        String nickname;
        String bio;
    }
}
