package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.MemberDTO;

public interface FollowingService {
    MemberDTO.GetFollowMemberResult getFollowPeople(Long memberId, String clokeyId, Integer page, Boolean isFollow);
}
