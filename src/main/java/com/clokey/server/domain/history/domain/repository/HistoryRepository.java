package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT h FROM History h WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :yearMonth")
    List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth);

    @Query("SELECT h FROM History h WHERE h.member.id = :memberId AND h.historyDate >= :weekAgo")
    List<History> findHistoriesWithinWeek(@Param("memberId") Long memberId, @Param("weekAgo") LocalDate weekAgo);

    @Query("UPDATE History h SET h.likes = h.likes + 1 WHERE h.id = :historyId")
    @Modifying(clearAutomatically = true)
    void incrementLikes(Long historyId);

    @Query("UPDATE History h SET h.likes = h.likes - 1 WHERE h.id = :historyId")
    @Modifying(clearAutomatically = true)
    void decrementLikes(Long historyId);

    boolean existsByHistoryDateAndMember_Id(LocalDate historyDate, Long memberId);

    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Member m " +
            "LEFT JOIN History h ON m.id = h.member.id AND h.historyDate = :historyDate " +
            "WHERE m.id IN :memberIds " +
            "GROUP BY m.id " +
            "ORDER BY m.id")
    List<Boolean> existsByHistoryDateAndMemberIds(@Param("historyDate") LocalDate historyDate, @Param("memberIds") List<Long> memberIds);


    Optional<History> findByHistoryDateAndMember_Id(LocalDate historyDate, Long memberId);

    Page<History> findByMemberInAndVisibilityOrderByHistoryDateDesc(List<Member> member, Visibility visibility, Pageable pageable);

    List<History> findTop6ByMemberInAndVisibilityOrderByHistoryDateDesc(List<Member> member, Visibility visibility);


    @Query("SELECT DISTINCT h FROM History h " +
            "JOIN HashtagHistory hh ON hh.history.id = h.id " +
            "LEFT JOIN Follow f ON f.following.id = h.member.id AND f.followed.id = :currentMemberId " +  // follow 관계 조인
            "WHERE hh.hashtag.id IN :hashtagIds " +
            "AND h.member.id <> :currentMemberId " +
            "AND f.id IS NULL " +  // 팔로우한 사람 제외
            "ORDER BY h.likes DESC, h.historyDate DESC")
    List<History> findTop10MembersByHashtagIdsOrderByLikes(
            @Param("hashtagIds") List<Long> hashtagIds,
            @Param("currentMemberId") Long currentMemberId,
            Pageable pageable);

    List<History> findAll();

    @Query("SELECT COUNT(h) FROM History h WHERE h.member = :member")
    Long countHistoryByMember(@Param("member") Member member);
}
