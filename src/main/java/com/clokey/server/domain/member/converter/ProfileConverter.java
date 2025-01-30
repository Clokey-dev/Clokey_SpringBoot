package com.clokey.server.domain.member.converter;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;

import java.time.LocalDateTime;


public class ProfileConverter {

    public static MemberDTO.ProfileRP toProfileRPDTO(Member member) {
        return MemberDTO.ProfileRP.builder()
                .id(member.getId())
                .bio(member.getBio())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .clokeyId(member.getClokeyId())
                .profileImageUrl(member.getProfileImageUrl())
                .profileBackImageUrl(member.getProfileBackImageUrl())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

