package com.clokey.server.domain.member.domain.repository;

import com.clokey.server.domain.member.domain.entity.Follow;
import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
            "FROM Follow f WHERE f.followed.id = :followedId AND f.following IN :members " +
            "GROUP BY f.following.id ORDER BY f.following.id")
    List<Boolean> checkFollowingStatus(@Param("followedId") Long followedId, @Param("members") List<Member> members);
}
