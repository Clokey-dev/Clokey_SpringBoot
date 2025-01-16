package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.dto.MemberResponseDTO;


public interface ProfileCommandService {
    MemberResponseDTO.ProfileRP updateProfile(Long userId, MemberResponseDTO.ProfileRQ request);
}
