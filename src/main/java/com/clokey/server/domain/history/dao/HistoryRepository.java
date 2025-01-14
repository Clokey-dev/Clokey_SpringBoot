package com.clokey.server.domain.history.dao;

import com.clokey.server.domain.model.History;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT h FROM History h WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :yearMonth")
    List<History> findHistoriesByMemberAndYearMonth(@Param("memberId") Long memberId, @Param("yearMonth") String yearMonth);

    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.likes = h.likes + 1 WHERE h.id = :historyId")
    void incrementLikes(Long historyId);

    // Likes 감소
    @Transactional
    @Modifying
    @Query("UPDATE History h SET h.likes = h.likes - 1 WHERE h.id = :historyId")
    void decrementLikes(Long historyId);
}
