package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.MemberDTO;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void follow(MemberDTO.FollowRQ request);

    MemberDTO.FollowRP followCheck(MemberDTO.FollowRQ request);

    MemberDTO.GetUserRP getUser(String clokeyId, Member currentUser); // 로그인한 사용자 정보 추가

    MemberDTO.ProfileRP updateProfile(Long userId, MemberDTO.ProfileRQ request,
                                      MultipartFile profileImage, MultipartFile profileBackImage);
}
