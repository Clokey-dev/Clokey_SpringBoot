package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Follow;
import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FollowRepositoryService {

    List<Boolean> checkFollowingStatus(Long followedId, List<Member> members);

    List<Member> findFollowedByFollowingId(Long followingId);

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

    List<Member> findFollowingByFollowedId(Long followedId);

    Optional<Follow> findByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

    void delete(Follow follow);

    void save(Follow follow);

    boolean isFollowing(Member currentUser, Member targetUser);

    Long countFollowersByMember(Member member);

    Long countFollowingByMember(Member member);

}
