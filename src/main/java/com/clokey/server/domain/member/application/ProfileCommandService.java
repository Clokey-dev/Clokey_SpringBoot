package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.dto.MemberDTO;
import org.springframework.web.multipart.MultipartFile;


public interface ProfileCommandService {
    MemberDTO.ProfileRP updateProfile(Long userId, MemberDTO.ProfileRQ request,
                                      MultipartFile profileImage, MultipartFile profileBackImage);
}
