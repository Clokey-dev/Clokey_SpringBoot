package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.History;
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

    @Query("UPDATE History h SET h.likes = h.likes + 1 WHERE h.id = :historyId")
    @Modifying
    void incrementLikes(Long historyId);

    @Query("UPDATE History h SET h.likes = h.likes - 1 WHERE h.id = :historyId")
    @Modifying
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
}
