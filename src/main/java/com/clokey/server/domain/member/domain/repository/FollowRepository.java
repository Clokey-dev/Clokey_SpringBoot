package com.clokey.server.domain.member.domain.repository;

import com.clokey.server.domain.member.domain.entity.Follow;
import com.clokey.server.domain.member.domain.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

    @Query("SELECT CASE WHEN EXISTS (SELECT 1 FROM Follow f WHERE f.following = m AND f.followed.id = :followedId) THEN true ELSE false END " +
            "FROM Member m WHERE m IN :members")
    List<Boolean> checkFollowingStatus(@Param("followedId") Long followedId, @Param("members") List<Member> members);

    Optional<Follow> findByFollowing_IdAndFollowed_Id(Long followingId, Long followedId);

    @Query("SELECT f.followed FROM Follow f WHERE f.following.id = :followingId")
    List<Member> findFollowedByFollowingId(@Param("followingId") Long followingId);

    @Modifying
    @Query("DELETE FROM Follow f WHERE f.following.id = :memberId OR f.followed.id = :memberId")
    void deleteByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT f.following FROM Follow f WHERE f.followed.id = :followedId")
    List<Member> findFollowingByFollowedId(@Param("followedId") Long followedId);

    @Query("SELECT COUNT(f) > 0 FROM Follow f WHERE f.following = :currentUser AND f.followed = :targetUser")
    boolean isFollowing(@Param("currentUser") Member currentUser, @Param("targetUser") Member targetUser);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.followed = :member")
    Long countFollowersByMember(@Param("member") Member member);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.following = :member")
    Long countFollowingByMember(@Param("member") Member member);

    @Query("SELECT f.followed FROM Follow f WHERE f.following.id = :followingId")
    List<Member> findFollowedByFollowingId(@Param("followingId")Long followingId, Pageable pageable);

    @Query("SELECT f.following FROM Follow f WHERE f.followed.id = :followedId")
    List<Member> findFollowingByFollowedId(@Param("followedId") Long followedId, Pageable pageable);
}
