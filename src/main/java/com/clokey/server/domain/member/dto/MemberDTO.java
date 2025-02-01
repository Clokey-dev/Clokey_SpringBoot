package com.clokey.server.domain.member.dto;

import com.clokey.server.domain.member.exception.annotation.EssentialFieldNotNull;
import com.clokey.server.domain.member.exception.annotation.IdValid;
import com.clokey.server.domain.model.entity.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberDTO {

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
        String profileBackImageUrl;
        String visibility;
        boolean isFollowing;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProfileRQ {

        @EssentialFieldNotNull
        String nickname;

        @EssentialFieldNotNull
        String clokeyId;

        String profileImageUrl;

        private String bio;

        private String profileBackImageUrl;

        Visibility visibility;

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
        Visibility visibility;
        LocalDateTime updatedAt;
    }



    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  FollowRQ{

        @IdValid
        String myClokeyId;
        @IdValid
        String yourClokeyId;

    }


    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class  FollowRP{

        boolean isFollow;

    }
}
