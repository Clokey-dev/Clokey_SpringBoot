package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.MemberDTO;

public interface FollowCommandService {
    void follow(MemberDTO.FollowRQ request);

    MemberDTO.FollowRP followCheck(MemberDTO.FollowRQ request);
}

