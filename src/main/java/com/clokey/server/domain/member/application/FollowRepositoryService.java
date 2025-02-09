package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepositoryService {

    List<Boolean> checkFollowingStatus(Long followedId, List<Member> members);

    List<Member> findFollowedByFollowingId(Long followingId);

    List<Member> findFollowingByFollowedId(Long followedId);
}
