package com.clokey.server.domain.history.domain.repository;

import com.clokey.server.domain.history.domain.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    @Query("SELECT h FROM History h WHERE h.member.id = :memberId AND FUNCTION('DATE_FORMAT', h.historyDate, '%Y-%m') = :yearMonth")
    List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth);

    @Query("UPDATE History h SET h.likes = h.likes + 1 WHERE h.id = :historyId")
    void incrementLikes(Long historyId);

    @Query("UPDATE History h SET h.likes = h.likes - 1 WHERE h.id = :historyId")
    void decrementLikes(Long historyId);

    boolean existsByHistoryDateAndMember_Id(LocalDate historyDate, Long memberId);

    Optional<History> findByHistoryDateAndMember_Id(LocalDate historyDate, Long memberId);
}
