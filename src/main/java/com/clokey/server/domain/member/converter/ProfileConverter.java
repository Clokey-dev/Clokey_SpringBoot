package com.clokey.server.domain.member.converter;

import com.clokey.server.domain.member.dto.MemberResponseDTO;
import com.clokey.server.domain.member.domain.entity.Member;

import java.time.LocalDateTime;


public class ProfileConverter {

    public static MemberResponseDTO.ProfileRP toProfileRPDTO(Member member) {
        return MemberResponseDTO.ProfileRP.builder()
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

