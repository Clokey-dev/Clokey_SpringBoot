package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepositoryService {

    List<Boolean> checkFollowingStatus(Long followedId, List<Member> members);

    List<Member> findFollowedByFollowingId(Long followingId);

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

    List<Member> findFollowingByFollowedId(Long followedId);

    List<Member> findFollowedByFollowingId(Long followingId, Pageable pageable);

    List<Member> findFollowingByFollowedId(Long followedId, Pageable pageable);
}
