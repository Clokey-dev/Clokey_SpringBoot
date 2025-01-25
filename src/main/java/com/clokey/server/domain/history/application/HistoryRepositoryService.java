package com.clokey.server.domain.history.application;

import com.clokey.server.domain.history.domain.entity.History;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepositoryService {

    List<History> findHistoriesByMemberAndYearMonth(Long memberId, String yearMonth);

    void incrementLikes(Long historyId);

    void decrementLikes(Long historyId);

    History findById(Long historyId);

    boolean existsById(Long historyId);

    History save(History history);

    boolean checkHistoryExistOfDate(LocalDate date, Long memberId);
}
