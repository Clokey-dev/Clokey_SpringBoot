package com.clokey.server.domain.follow.dao;

import com.clokey.server.domain.model.Cloth;
import com.clokey.server.domain.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

}
