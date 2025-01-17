package com.clokey.server.domain.member.converter;

import com.clokey.server.domain.member.dto.MemberDTO;
import com.clokey.server.domain.model.Member;

public class GetUserConverter {

    public static MemberDTO.GetUserRP toGetUserResponseDTO(Member member, Long recordCount, Long followerCount, Long followingCount) {
        return MemberDTO.GetUserRP.builder()
                .clokeyId(member.getClokeyId())
                .profileImageUrl(member.getProfileImageUrl())
                .recordCount(recordCount)
                .followerCount(followerCount)
                .followingCount(followingCount)
                .nickname(member.getNickname())
                .bio(member.getBio())
                .visibility(member.getVisibility().toString())
                .build();
    }
}


