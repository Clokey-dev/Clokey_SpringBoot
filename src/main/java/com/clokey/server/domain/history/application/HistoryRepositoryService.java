package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.History;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepositoryService {

    List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth);

    List<History> findHistoriesByMemberWithinWeek(Long memberId);

    void incrementLikes(Long historyId);

    void decrementLikes(Long historyId);

    History findById(Long historyId);

    boolean existsById(Long historyId);

    History save(History history);

    boolean checkHistoryExistOfDate(LocalDate date, Long memberId);

    History getHistoryOfDate(LocalDate date, Long memberId);

    void deleteById(Long historyId);

    List<Boolean> existsByHistoryDateAndMemberIds(LocalDate historyDate, List<Long> memberIds);


    void deleteByHistoryIds(List<Long> historyIds);
}
