package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.dto.ProfileResponseDTO;
import com.clokey.server.domain.member.dto.ProfileRequestDTO;


public interface ProfileCommandService {
    ProfileResponseDTO.ProfileRP updateProfile(Long userId, ProfileRequestDTO.ProfileRQ request);
}
