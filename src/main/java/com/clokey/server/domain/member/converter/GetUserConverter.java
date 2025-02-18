package com.clokey.server.domain.member.converter;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;


public class GetUserConverter {

    public static MemberDTO.GetUserRP toGetUserResponseDTO(Member member, Long recordCount, Long followerCount, Long followingCount, Boolean isFollowing
            , String clothImage1, String clothImage2, String clothImage3) {
        return MemberDTO.GetUserRP.builder()
                .clokeyId(member.getClokeyId())
                .profileImageUrl(member.getProfileImageUrl())
                .recordCount(recordCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .nickname(member.getNickname())
                .bio(member.getBio())
                .profileBackImageUrl(member.getProfileBackImageUrl())
                .visibility(member.getVisibility().toString())
                .isFollowing(isFollowing) // 추가된 필드 반영
                .clothImage1(clothImage1)
                .clothImage2(clothImage2)
                .clothImage3(clothImage3)
                .build();
    }
}



