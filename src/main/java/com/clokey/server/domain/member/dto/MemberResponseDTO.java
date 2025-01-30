package com.clokey.server.domain.member.dto;

import com.clokey.server.domain.member.exception.annotation.EssentialFieldNotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberResponseDTO {

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
        String visibility;
        boolean isFollowing;
    }

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

        private String profileBackImageUrl;
    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileRP {

        Long id;
        String bio;
        String email;
        String nickname;
        String clokeyId;
        String profileImageUrl;
        String profileBackImageUrl;
        LocalDateTime updatedAt;
    }
}
