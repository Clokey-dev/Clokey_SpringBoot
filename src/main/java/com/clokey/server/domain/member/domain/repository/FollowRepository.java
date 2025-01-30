package com.clokey.server.domain.member.domain.repository;

import com.clokey.server.domain.member.domain.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);


    Optional<Follow> findByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);
}
