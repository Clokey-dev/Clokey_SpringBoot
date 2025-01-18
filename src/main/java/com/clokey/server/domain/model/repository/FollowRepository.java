package com.clokey.server.domain.model.repository;

import com.clokey.server.domain.model.entity.mapping.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

}
