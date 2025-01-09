package com.clokey.server.domain.follow.dao;

import com.clokey.server.domain.model.mapping.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

}
