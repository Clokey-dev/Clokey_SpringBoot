package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Member;

import java.util.List;

public interface FollowRepositoryService {

    List<Boolean> checkFollowingStatus(Long followedId, List<Member> members);
}
