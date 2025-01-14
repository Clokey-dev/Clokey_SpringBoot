package com.clokey.server.domain.member.converter;

import com.clokey.server.domain.member.dto.ProfileResponseDTO;
import com.clokey.server.domain.model.Member;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;


public class ProfileConverter {

    public static ProfileResponseDTO.ProfileRP toProfileRPDTO(Member member) {
        return ProfileResponseDTO.ProfileRP.builder()
                .id(member.getId())
                .bio(member.getBio())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .clokeyId(member.getClokeyId())
                .profileImageUrl(member.getProfileImageUrl())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}

