package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.dto.MemberDTO;


public interface ProfileCommandService {
    MemberDTO.ProfileRP updateProfile(Long userId, MemberDTO.ProfileRQ request);
}
